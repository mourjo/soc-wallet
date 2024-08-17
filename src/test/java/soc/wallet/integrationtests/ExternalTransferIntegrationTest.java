package soc.wallet.integrationtests;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import soc.wallet.testutils.DbHelpers;
import soc.wallet.testutils.HttpHelpers;
import soc.wallet.web.Launcher;

public class ExternalTransferIntegrationTest {

	final Javalin app = Launcher.buildApp();

	@Test
	void validExternalTransfer() {
		var account = DbHelpers.insertAccount("INR");
		Assertions.assertEquals(0, DbHelpers.getBalance(account));

		JavalinTest.test(app, (server, client) -> {
			var response = client.post(
					"/transfer/external",
					HttpHelpers.externalTransferRequest(account, "100"),
					HttpHelpers.headers()
			);
			Assertions.assertEquals(201, response.code());
			var body = TypeConversion.toExternalTransferCreationResponse(response);
			Assertions.assertEquals("100.00", body.balance());
			Assertions.assertEquals("INR", body.currency().toString());
			Assertions.assertEquals(account.getId(), body.accountId());
		});

		Assertions.assertEquals(100, DbHelpers.getBalance(account));
	}

	@Test
	void multipleExternalTransfers() {
		var account = DbHelpers.insertAccount("INR");
		Assertions.assertEquals(0, DbHelpers.getBalance(account));

		var transfers = List.of(
				HttpHelpers.externalTransferRequest(account, "50"),
				HttpHelpers.externalTransferRequest(account, "499.50"),
				HttpHelpers.externalTransferRequest(account, "-99")
		);

		JavalinTest.test(app, (server, client) -> {
			for (var transferRequest : transfers) {
				client.post(
						"/transfer/external",
						transferRequest,
						HttpHelpers.headers()
				);
			}
		});

		Assertions.assertEquals(450.5, DbHelpers.getBalance(account));
	}

	@Test
	void incompatibleCurrencyExternalTransfer() {
		var account = DbHelpers.insertAccount("EUR");
		Assertions.assertEquals(0, DbHelpers.getBalance(account));

		JavalinTest.test(app, (server, client) -> {
			var response = client.post(
					"/transfer/external",
					HttpHelpers.externalTransferRequest(account.getId(), "INR", "100"),
					HttpHelpers.headers()
			);
			Assertions.assertEquals(400, response.code());
			var body = TypeConversion.toErrorResponse(response);
			Assertions.assertEquals("Transfer currency and account currency cannot be different",
					body.message());
		});
	}

	@Test
	void negativeBalanceExternalTransfer() {
		var account = DbHelpers.insertAccount("EUR");
		Assertions.assertEquals(0, DbHelpers.getBalance(account));

		JavalinTest.test(app, (server, client) -> {
			var response = client.post(
					"/transfer/external",
					HttpHelpers.externalTransferRequest(account, "-50"),
					HttpHelpers.headers()
			);
			Assertions.assertEquals(400, response.code());
			var body = TypeConversion.toErrorResponse(response);
			Assertions.assertEquals("There is not enough balance to execute this transfer",
					body.message());
		});
	}

	@Test
	void invalidAccountExternalTransfer() {
		var account = DbHelpers.insertAccount("EUR");
		Assertions.assertEquals(0, DbHelpers.getBalance(account));

		JavalinTest.test(app, (server, client) -> {
			var response = client.post(
					"/transfer/external",
					HttpHelpers.externalTransferRequest(999888777, "EUR", "50"),
					HttpHelpers.headers()
			);
			Assertions.assertEquals(400, response.code());
			var body = TypeConversion.toErrorResponse(response);
			Assertions.assertEquals("Account does not exist", body.message());
		});
	}
}
