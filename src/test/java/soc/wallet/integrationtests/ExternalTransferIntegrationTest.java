package soc.wallet.integrationtests;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import java.math.BigDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import soc.wallet.testutils.DbHelpers;
import soc.wallet.testutils.HttpHelpers;
import soc.wallet.web.Launcher;
import soc.wallet.web.dto.ExternalTransferCreationRequest;

public class ExternalTransferIntegrationTest {

	final Javalin app = Launcher.buildApp();

	@Test
	void createExternalTransfer() {
		var user = DbHelpers.insertUser("Monty");
		var account = DbHelpers.insertAccount("EUR", user.getId());
		Assertions.assertTrue(0 == BigDecimal.ZERO.compareTo(account.getBalance()));

		JavalinTest.test(app, (server, client) -> {
			var req = new ExternalTransferCreationRequest(account.getId(), "100", "SBI");
			var response = client.post(
					"/transfer/external",
					req,
					HttpHelpers.headers()
			);
			Assertions.assertEquals(201, response.code());
		});
		var updatedAccount = DbHelpers.getAccount(account.getId());
		Assertions.assertTrue(0 == new BigDecimal(100).compareTo(updatedAccount.getBalance()));
	}

}
