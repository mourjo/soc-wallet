package soc.wallet.integrationtests;

import static soc.wallet.common.Constants.AUTH_HEADER_NAME;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import soc.wallet.testutils.HttpHelpers;
import soc.wallet.web.Launcher;
import soc.wallet.web.dto.UserCreationRequest;

public class UserIntegrationTest {

	final Javalin app = Launcher.buildApp();

	@Test
	void createUser() {
		String email = UUID.randomUUID() + "@gmail.com";
		String name = "Joe";

		JavalinTest.test(app, (server, client) -> {
			var response = client.post(
					"/user",
					new UserCreationRequest(email, name),
					HttpHelpers.headers()
			);
			Assertions.assertEquals(201, response.code());
			var body = TypeConversion.toUserCreationResponse(response);
			Assertions.assertTrue(body.id() > 0);
		});
	}

	@Test
	void createDuplicateUser() {
		String email = UUID.randomUUID() + "@gmail.com";
		String name = "Joe";

		JavalinTest.test(app, (server, client) -> {
			Assertions.assertEquals(201,
					client.post(
							"/user",
							new UserCreationRequest(email, name),
							HttpHelpers.headers()
					).code());

			var response = client.post(
					"/user",
					new UserCreationRequest(email, name),
					HttpHelpers.headers()
			);
			Assertions.assertEquals(409, response.code());
			var body = TypeConversion.toErrorResponse(response);
			Assertions.assertEquals("User already exists", body.message());
		});
	}

	@Test
	void fetchUser() {
		String email = UUID.randomUUID() + "@gmail.com";
		String name = "Calvin";

		JavalinTest.test(app, (server, client) -> {
			var id = TypeConversion.toUserCreationResponse(
					client.post(
							"/user",
							new UserCreationRequest(email, name),
							HttpHelpers.headers()
					)
			).id();
			var response = client.get("/user/" + id, HttpHelpers.headers());
			Assertions.assertEquals(200, response.code());
			var body = TypeConversion.toUserFetchResponse(response);
			Assertions.assertEquals(email, body.email());
			Assertions.assertEquals(name, body.name());
			Assertions.assertEquals(id, body.id());
		});
	}

	@Test
	void fetchUserWithInvalidId() {
		JavalinTest.test(app, (server, client) -> {
			var response = client.get("/user/thisshouldnotwork", HttpHelpers.headers());
			Assertions.assertEquals(400, response.code());
			var body = TypeConversion.toErrorResponse(response);
			Assertions.assertEquals("Invalid number in request", body.message());
		});
	}

	@Test
	void fetchUserWithNonExistentId() {
		JavalinTest.test(app, (server, client) -> {
			var response = client.get("/user/999999", HttpHelpers.headers());
			Assertions.assertEquals(404, response.code());
			var body = TypeConversion.toErrorResponse(response);
			Assertions.assertEquals("User does not exist", body.message());
		});
	}

	@Test
	void unauthenticatedCreation() {
		String email = UUID.randomUUID() + "@gmail.com";
		String name = "Jon";

		JavalinTest.test(app, (server, client) -> {
			var response = client.post("/user", new UserCreationRequest(email, name));
			Assertions.assertEquals(401, response.code());
			var body = TypeConversion.toErrorResponse(response);
			Assertions.assertEquals("Invalid Authentication", body.message());
		});
	}

	@Test
	void badAuthTokenCreation() {
		String email = UUID.randomUUID() + "@gmail.com";
		String name = "Jon";

		JavalinTest.test(app, (server, client) -> {
			var response = client.post("/user", new UserCreationRequest(email, name),
					req -> req.header(AUTH_HEADER_NAME, "BAD_VALUE"));
			Assertions.assertEquals(401, response.code());
			var body = TypeConversion.toErrorResponse(response);
			Assertions.assertEquals("Invalid Authentication", body.message());
		});
	}

	@Test
	void unauthenticatedFetch() {
		JavalinTest.test(app, (server, client) -> {
			var response = client.get("/user/1");
			Assertions.assertEquals(401, response.code());
			var body = TypeConversion.toErrorResponse(response);
			Assertions.assertEquals("Invalid Authentication", body.message());
		});
	}

	@Test
	void badAuthTokenFetch() {
		JavalinTest.test(app, (server, client) -> {
			var response = client.get("/user/1", req -> req.header(AUTH_HEADER_NAME, "BAD_VALUE"));
			Assertions.assertEquals(401, response.code());
			var body = TypeConversion.toErrorResponse(response);
			Assertions.assertEquals("Invalid Authentication", body.message());
		});
	}
}
