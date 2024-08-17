package soc.wallet.integrationtests;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import soc.wallet.testutils.DbHelpers;
import soc.wallet.testutils.HttpHelpers;
import soc.wallet.web.Launcher;
import soc.wallet.web.dto.SupportedCurrency;

public class InternalTransferIntegrationTest {

	final Javalin app = Launcher.buildApp();

	@Test
	void validExternalTransfer() {
		var sourceAccount = DbHelpers.insertAccount(1000, "EUR");
		var destinationAccount = DbHelpers.insertAccount(0,"EUR");

		JavalinTest.test(app, (server, client) -> {
			var response = client.post(
					"/transfer/internal",
					HttpHelpers.internalTransferRequest(sourceAccount, destinationAccount, "50"),
					HttpHelpers.headers()
			);
			Assertions.assertEquals(201, response.code());
			var body = TypeConversion.toInternalTransferCreationResponse(response);
			Assertions.assertEquals("50.00", body.destinationBalance());
			Assertions.assertEquals("950.00", body.sourceBalance());
			Assertions.assertEquals(sourceAccount.getId(), body.sourceAccountId());
			Assertions.assertEquals(destinationAccount.getId(), body.destinationAccountId());
			Assertions.assertEquals(SupportedCurrency.EUR, body.currency());
		});

		Assertions.assertEquals(950, DbHelpers.getBalance(sourceAccount));
		Assertions.assertEquals(50, DbHelpers.getBalance(destinationAccount));
	}

	@Test
	void multipleValidExternalTransfers() {
		var a1 = DbHelpers.insertAccount(700, "EUR");
		var a2 = DbHelpers.insertAccount(0,"EUR");
		var a3 = DbHelpers.insertAccount(50,"EUR");
		var totalMoneyBefore = DbHelpers.getBalance(a1) + DbHelpers.getBalance(a2) + DbHelpers.getBalance(a3);

		JavalinTest.test(app, (server, client) -> {
			client.post(
					"/transfer/internal",
					HttpHelpers.internalTransferRequest(a1, a2, "50"),
					HttpHelpers.headers()
			);

			client.post(
					"/transfer/internal",
					HttpHelpers.internalTransferRequest(a1, a3, "200"),
					HttpHelpers.headers()
			);

			client.post(
					"/transfer/internal",
					HttpHelpers.internalTransferRequest(a3, a2, "100"),
					HttpHelpers.headers()
			);

			client.post(
					"/transfer/internal",
					HttpHelpers.internalTransferRequest(a2, a1, "10"),
					HttpHelpers.headers()
			);

		});

		Assertions.assertEquals(700-50-200+10, DbHelpers.getBalance(a1));
		Assertions.assertEquals(50+100-10, DbHelpers.getBalance(a2));
		Assertions.assertEquals(50+200-100, DbHelpers.getBalance(a3));

		var totalMoneyAfter = DbHelpers.getBalance(a1) + DbHelpers.getBalance(a2) + DbHelpers.getBalance(a3);

		Assertions.assertEquals(totalMoneyBefore, totalMoneyAfter);
	}

	@Test
	void invalidSourceAccountExternalTransfer() {
		var account = DbHelpers.insertAccount(100, "EUR");
		JavalinTest.test(app, (server, client) -> {
			var response = client.post(
					"/transfer/internal",
					HttpHelpers.internalTransferRequest(112233, account.getId(), "EUR", "50"),
					HttpHelpers.headers()
			);

			Assertions.assertEquals(400, response.code());
			var body = TypeConversion.toErrorResponse(response);
			Assertions.assertEquals("Account does not exist", body.message());

		});
	}

	@Test
	void invalidDestinationAccountExternalTransfer() {
		var account = DbHelpers.insertAccount(100, "EUR");
		JavalinTest.test(app, (server, client) -> {
			var response = client.post(
					"/transfer/internal",
					HttpHelpers.internalTransferRequest( account.getId(),111111111, "EUR", "50"),
					HttpHelpers.headers()
			);

			Assertions.assertEquals(400, response.code());
			var body = TypeConversion.toErrorResponse(response);
			Assertions.assertEquals("Account does not exist", body.message());

		});
	}

	@Test
	void insufficientBalanceExternalTransfer() {
		var source = DbHelpers.insertAccount();
		var destination = DbHelpers.insertAccount(1000, "EUR");

		JavalinTest.test(app, (server, client) -> {
			var response = client.post(
					"/transfer/internal",
					HttpHelpers.internalTransferRequest(source, destination, "50"),
					HttpHelpers.headers()
			);

			Assertions.assertEquals(400, response.code());
			var body = TypeConversion.toErrorResponse(response);
			Assertions.assertEquals("There is not enough balance to execute this transfer", body.message());

		});
	}

	@Test
	void selfExternalTransfer() {
		var source = DbHelpers.insertAccount(1000,"EUR");

		JavalinTest.test(app, (server, client) -> {
			var response = client.post(
					"/transfer/internal",
					HttpHelpers.internalTransferRequest(source, source, "50"),
					HttpHelpers.headers()
			);

			Assertions.assertEquals(400, response.code());
			var body = TypeConversion.toErrorResponse(response);
			Assertions.assertEquals("Self transfers are not allowed", body.message());

		});
	}

	@Test
	void negativeExternalTransfer() {
		var source = DbHelpers.insertAccount(1000,"EUR");

		JavalinTest.test(app, (server, client) -> {
			var response = client.post(
					"/transfer/internal",
					HttpHelpers.internalTransferRequest(source, source, "-50"),
					HttpHelpers.headers()
			);

			Assertions.assertEquals(400, response.code());
			var body = TypeConversion.toErrorResponse(response);
			Assertions.assertEquals("Negative transfers are not allowed", body.message());

		});
	}
}
