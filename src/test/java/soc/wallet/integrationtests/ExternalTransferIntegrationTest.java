package soc.wallet.integrationtests;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import soc.wallet.testutils.DbHelpers;
import soc.wallet.testutils.HttpHelpers;
import soc.wallet.web.Launcher;
import soc.wallet.web.dto.ExternalTransferCreationRequest;
import soc.wallet.web.dto.SupportedCurrency;

public class ExternalTransferIntegrationTest {

	final Javalin app = Launcher.buildApp();

	@Test
	void validExternalTransfer() {
		var user = DbHelpers.insertUser("Monty");
		var account = DbHelpers.insertAccount("INR", user.getId());
		Assertions.assertEquals(0, BigDecimal.ZERO.compareTo(account.getBalance()));

		JavalinTest.test(app, (server, client) -> {
			var req = new ExternalTransferCreationRequest(account.getId(), SupportedCurrency.INR, "100", "SBI");
			var response = client.post(
					"/transfer/external",
					req,
					HttpHelpers.headers()
			);
			Assertions.assertEquals(201, response.code());
			var body = TypeConversion.toExternalTransferCreationResponse(response);
			Assertions.assertEquals("100.00", body.balance());
			Assertions.assertEquals("INR", body.currency().toString());
			Assertions.assertEquals(account.getId(), body.accountId());
		});
		var updatedAccount = DbHelpers.getAccount(account.getId());
		Assertions.assertEquals(0, new BigDecimal(100).compareTo(updatedAccount.getBalance()));
	}

	@Test
	void multipleExternalTransfers() {
		var user = DbHelpers.insertUser("Monty");
		var account = DbHelpers.insertAccount("INR", user.getId());
		Assertions.assertEquals(0, BigDecimal.ZERO.compareTo(account.getBalance()));

		var transfers = List.of(
				HttpHelpers.externalTransferRequest(account, SupportedCurrency.INR, "50"),
				HttpHelpers.externalTransferRequest(account, SupportedCurrency.INR, "499.50"),
				HttpHelpers.externalTransferRequest(account, SupportedCurrency.INR, "-99")
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

		var updatedAccount = DbHelpers.getAccount(account.getId());
		Assertions.assertEquals(0, new BigDecimal("450.5").compareTo(updatedAccount.getBalance()));
	}

	@Test
	void incompatibleCurrencyExternalTransfer() {
		var user = DbHelpers.insertUser("Monty");
		var account = DbHelpers.insertAccount("EUR", user.getId());
		Assertions.assertEquals(0, BigDecimal.ZERO.compareTo(account.getBalance()));

		JavalinTest.test(app, (server, client) -> {
			var req = new ExternalTransferCreationRequest(account.getId(), SupportedCurrency.INR, "100", "SBI");
			var response = client.post(
					"/transfer/external",
					req,
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
		var user = DbHelpers.insertUser("Monty");
		var account = DbHelpers.insertAccount("EUR", user.getId());
		Assertions.assertEquals(0, BigDecimal.ZERO.compareTo(account.getBalance()));

		JavalinTest.test(app, (server, client) -> {
			var req = new ExternalTransferCreationRequest(account.getId(), SupportedCurrency.EUR, "-100", "SBI");
			var response = client.post(
					"/transfer/external",
					req,
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
		var user = DbHelpers.insertUser("Monty");
		var account = DbHelpers.insertAccount("EUR", user.getId());
		Assertions.assertEquals(0, BigDecimal.ZERO.compareTo(account.getBalance()));

		JavalinTest.test(app, (server, client) -> {
			var req = new ExternalTransferCreationRequest(999888777, SupportedCurrency.EUR,  "-100", "SBI");
			var response = client.post(
					"/transfer/external",
					req,
					HttpHelpers.headers()
			);
			Assertions.assertEquals(400, response.code());
			var body = TypeConversion.toErrorResponse(response);
			Assertions.assertEquals("Account does not exist", body.message());
		});
	}
}
