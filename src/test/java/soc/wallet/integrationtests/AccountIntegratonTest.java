package soc.wallet.integrationtests;

import static soc.wallet.common.Constants.AUTH_HEADER_NAME;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import soc.wallet.testutils.DbHelpers;
import soc.wallet.testutils.HttpHelpers;
import soc.wallet.web.Launcher;
import soc.wallet.web.dto.AccountCreationRequest;
import soc.wallet.web.dto.SupportedCurrency;

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
