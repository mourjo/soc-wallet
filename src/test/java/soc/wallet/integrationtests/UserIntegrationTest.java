package soc.wallet.integrationtests;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import soc.wallet.web.Launcher;
import soc.wallet.web.dto.UserCreationRequest;

public class UserIntegrationTest {

	final Javalin app = Launcher.buildApp();

	@Test
	void createUser() {
		String email = UUID.randomUUID() + "@gmail.com";
		String name = "Joe";

		JavalinTest.test(app, (server, client) -> {
			var response = client.put("/user", new UserCreationRequest(email, name));
			Assertions.assertEquals(201, response.code());
			var body = TypeConversion.toUserCreationResponse(response);
			Assertions.assertTrue(body.id() > 0);
		});
	}

	@Test
	void fetchUser() {
		String email = UUID.randomUUID() + "@gmail.com";
		String name = "Calvin";

		JavalinTest.test(app, (server, client) -> {
			var id = TypeConversion.toUserCreationResponse(
					client.put("/user", new UserCreationRequest(email, name))
			).id();
			var response = client.get("/user/" + id);
			Assertions.assertEquals(200, response.code());
			var body = TypeConversion.toUserFetchResponse(response);
			Assertions.assertEquals(email, body.email());
			Assertions.assertEquals(name, body.name());
			Assertions.assertEquals(id, body.id());
		});
	}
}
