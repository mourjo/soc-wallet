package soc.wallet.integrationtests;

import static soc.wallet.common.Constants.AUTH_HEADER_NAME;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import soc.wallet.testutils.DbHelpers;
import soc.wallet.testutils.HttpHelpers;
import soc.wallet.web.Launcher;
import soc.wallet.web.dto.AccountCreationRequest;
import soc.wallet.web.dto.SupportedCurrency;
import soc.wallet.web.dto.TransferInfo;
import soc.wallet.web.dto.TransferInfo.TransferType;
import soc.wallet.web.dto.UserCreationRequest;

public class AccountIntegratonTest {

	final Javalin app = Launcher.buildApp();

	@ParameterizedTest
	@EnumSource(SupportedCurrency.class)
	void createAccount(SupportedCurrency currency) {
		var userJill = DbHelpers.insertUser("Jill");

		JavalinTest.test(app, (server, client) -> {
			var response = client.post("/account",
					new AccountCreationRequest(userJill.getId(), currency),
					HttpHelpers.headers()
			);
			Assertions.assertEquals(201, response.code());
			var body = TypeConversion.toAccountCreationResponse(response);
			Assertions.assertTrue(body.id() > 0);
			Assertions.assertEquals(userJill.getEmail(), body.userEmail());
			Assertions.assertEquals("0.00", body.balance());
			Assertions.assertEquals(currency.toString(), body.currency());
		});
	}

	@Test
	void fetchAccount() {
		var alice = DbHelpers.insertUser("Alice");
		var aliceAccount = DbHelpers.insertAccount(0, "EUR", alice.getId());

		var bob = DbHelpers.insertUser("Bob");
		var bobAccount = DbHelpers.insertAccount(1000, "EUR", bob.getId());

		var john = DbHelpers.insertUser("John");
		var johnAccount = DbHelpers.insertAccount(0, "EUR", john.getId());

		JavalinTest.test(app, (server, client) -> {

			HttpHelpers.externalTransfer(client, aliceAccount, "100", "HDFC");
			HttpHelpers.internalTransfer(client, bobAccount, aliceAccount, "80");
			HttpHelpers.externalTransfer(client, aliceAccount, "-10", "SBI");
			HttpHelpers.internalTransfer(client, aliceAccount, johnAccount, "20");

			var aliceAccountInfo =  TypeConversion.toAccountFetchResponse( client.get("/account/" + aliceAccount.getId(), HttpHelpers.headers()));
			Assertions.assertEquals("150.00", aliceAccountInfo.balance());
			Assertions.assertEquals("EUR", aliceAccountInfo.currency());
			Assertions.assertEquals(alice.getEmail(), aliceAccountInfo.userEmail());
			Assertions.assertEquals(alice.getId(), aliceAccountInfo.userId());

			var aliceTransfers = aliceAccountInfo
					.transfers()
					.stream()
					.map(this::formatTransferInfo)
					.toList();
			Assertions.assertEquals(
					List.of(
							"INTERNAL_DEBIT___account:%d___-20.00".formatted(johnAccount.getId()),
							"EXTERNAL___SBI___-10.00",
							"INTERNAL_CREDIT___account:%d___80.00".formatted(bobAccount.getId()),
							"EXTERNAL___HDFC___100.00"),
					aliceTransfers
			);

			var bobAccountInfo = TypeConversion.toAccountFetchResponse( client.get("/account/" + bobAccount.getId(), HttpHelpers.headers()));
			Assertions.assertEquals("920.00", bobAccountInfo.balance());
			Assertions.assertEquals("EUR", bobAccountInfo.currency());
			Assertions.assertEquals(bob.getEmail(), bobAccountInfo.userEmail());
			Assertions.assertEquals(bob.getId(), bobAccountInfo.userId());

			var bobTransfers = bobAccountInfo
					.transfers()
					.stream()
					.map(this::formatTransferInfo)
					.toList();
			Assertions.assertEquals(
					List.of(
							"INTERNAL_DEBIT___account:%d___-80.00".formatted(aliceAccount.getId())
					),
					bobTransfers
			);

			var johnAccountInfo = TypeConversion.toAccountFetchResponse( client.get("/account/" + johnAccount.getId(), HttpHelpers.headers()));
			Assertions.assertEquals("20.00", johnAccountInfo.balance());
			Assertions.assertEquals("EUR", johnAccountInfo.currency());
			Assertions.assertEquals(john.getEmail(), johnAccountInfo.userEmail());
			Assertions.assertEquals(john.getId(), johnAccountInfo.userId());
			var johnTransfers = johnAccountInfo
					.transfers()
					.stream()
					.map(this::formatTransferInfo)
					.toList();
			Assertions.assertEquals(
					List.of(
							"INTERNAL_CREDIT___account:%d___20.00".formatted(aliceAccount.getId())
					),
					johnTransfers
			);
		});
	}

	@Test
	void fetchAccountWithNoTransfers() {
		var alice = DbHelpers.insertUser("Alice");
		var aliceAccount = DbHelpers.insertAccount(0, "INR", alice.getId());

		JavalinTest.test(app, (server, client) -> {
			var response = client.get("/account/" + aliceAccount.getId(), HttpHelpers.headers());
			Assertions.assertEquals(200, response.code());

			var body = TypeConversion.toAccountFetchResponse(response);

			Assertions.assertEquals("0.00", body.balance());
			Assertions.assertEquals("INR", body.currency());
			Assertions.assertEquals(alice.getEmail(), body.userEmail());
			Assertions.assertEquals(alice.getId(), body.userId());
			var transfers = body.transfers().stream().map(this::formatTransferInfo).toList();
			Assertions.assertEquals(
					List.of(),
					transfers
			);
		});
	}

	String formatTransferInfo(TransferInfo t) {
		String source = (t.transferType() != TransferType.EXTERNAL) ? ("account:"+ t.source()) : t.source();
		return String.join( "___",  t.transferType().toString(), source, t.amount());
	}


	@Test
	void unauthenticatedFetchAccount() {
		var alice = DbHelpers.insertUser("Alice");
		var aliceAccount = DbHelpers.insertAccount(0, "EUR", alice.getId());

		var bob = DbHelpers.insertUser("Bob");
		var bobAccount = DbHelpers.insertAccount(1000, "EUR", bob.getId());

		JavalinTest.test(app, (server, client) -> {

			HttpHelpers.externalTransfer(client, aliceAccount, "100");
			HttpHelpers.internalTransfer(client, bobAccount, aliceAccount, "80");
			HttpHelpers.externalTransfer(client, aliceAccount, "-10");

			var response = client.get("/account/" + aliceAccount.getId());
			Assertions.assertEquals(401, response.code());
		});
	}

	@Test
	void createAccountUnsupportedCurrency() {
		var userHellen = DbHelpers.insertUser("Hellen");

		JavalinTest.test(app, (server, client) -> {
			var response = client.post(
					"/account",
					Map.of(
							"currency", "NOT A VALID CURRENCY",
							"userId", userHellen.getId()
					),
					HttpHelpers.headers()
			);
			Assertions.assertEquals(400, response.code());
		});
	}

	@Test
	void createAccountForNonExistentUser() {
		JavalinTest.test(app, (server, client) -> {
			var response = client.post("/account",
					new AccountCreationRequest(99999, SupportedCurrency.EUR),
					HttpHelpers.headers());
			Assertions.assertEquals(400, response.code());
			var body = TypeConversion.toErrorResponse(response);
			Assertions.assertEquals("Account creation failed", body.message());
		});
	}

	@Test
	void unauthenticatedAccountCreation() {
		var userMary = DbHelpers.insertUser("Mary");

		JavalinTest.test(app, (server, client) -> {
			var response = client.post("/account",
					new AccountCreationRequest(userMary.getId(), SupportedCurrency.EUR));
			Assertions.assertEquals(401, response.code());
		});
	}

	@Test
	void badAuthTokenAccountCreation() {
		var userMary = DbHelpers.insertUser("Nelly");

		JavalinTest.test(app, (server, client) -> {
			var response = client.post("/account",
					new AccountCreationRequest(userMary.getId(), SupportedCurrency.EUR),
					req -> req.header(AUTH_HEADER_NAME, "BAD_VALUE"));
			Assertions.assertEquals(401, response.code());
		});
	}
}
