package soc.wallet.integrationtests;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import static soc.wallet.common.Constants.AUTH_HEADER_NAME;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import okhttp3.Request.Builder;
import soc.wallet.common.Environment;
import soc.wallet.entities.UserEntity;
import soc.wallet.web.Launcher;
import soc.wallet.web.dto.AccountCreationRequest;
import soc.wallet.web.dto.SupportedCurrency;
import soc.wallet.web.dto.UserCreationRequest;

public class AccountIntegratonTest {
	final Javalin app = Launcher.buildApp();

	@SneakyThrows
	private Connection getConnection() {
		String host = Environment.getPostgresHost();
		String port = Environment.getPostgresPort();
		String database = Environment.getPostgresDatabase();
		String username = Environment.getPostgresUser();
		String connectionString = "jdbc:postgresql://%s:%s/%s".formatted(host, port, database);
		return DriverManager.getConnection(connectionString, username, null);
	}

	@NotNull
	private static Consumer<Builder> headers() {
		return req -> req.header(AUTH_HEADER_NAME, Environment.getApiSecret());
	}

	static final AtomicInteger counter = new AtomicInteger(0);

	@Test
	void createAccount() {
		var userJill = insertUser("Jill");

		JavalinTest.test(app, (server, client) -> {
			var response = client.put("/account", new AccountCreationRequest(userJill.getId(), SupportedCurrency.EUR), headers());
			Assertions.assertEquals(201, response.code());
			var body = TypeConversion.toAccountCreationResponse(response);
			Assertions.assertTrue(body.id() > 0);
			Assertions.assertEquals(userJill.getEmail(), body.userEmail());
		});
	}

	@Test
	void createAccountForNonExistentUser() {
		JavalinTest.test(app, (server, client) -> {
			var response = client.put("/account", new AccountCreationRequest(99999, SupportedCurrency.EUR), headers());
			Assertions.assertEquals(400, response.code());
			var body = TypeConversion.toErrorResponse(response);
			Assertions.assertEquals("Account creation failed", body.message());
		});
	}

	@Test
	void unauthenticatedAccountCreation() {
		var userMary = insertUser("Mary");

		JavalinTest.test(app, (server, client) -> {
			var response = client.put("/account", new AccountCreationRequest(userMary.getId(), SupportedCurrency.EUR));
			Assertions.assertEquals(401, response.code());
		});
	}


	@SneakyThrows
	private UserEntity insertUser(String prefix) {
		int c = counter.incrementAndGet();
		String email = prefix + "-" + c + "-" + UUID.randomUUID() + "@gmail.com";
		String name = prefix + "-" + c;

		try (Connection conn = getConnection()) {
			return DSL.using(conn, SQLDialect.POSTGRES)
					.insertInto(table("users"))
					.columns(field("name"), field("email"))
					.values(name, email)
					.returningResult(field("id"), field("name"), field("email"), field("created_at", OffsetDateTime.class))
					.fetchAnyInto(UserEntity.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
