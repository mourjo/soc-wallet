package soc.wallet.web;

import static org.jooq.impl.DSL.asterisk;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import soc.wallet.common.Environment;
import soc.wallet.entities.UserEntity;
import soc.wallet.exceptions.UserAlreadyExistsException;
import soc.wallet.exceptions.UserNotFoundException;
import soc.wallet.web.dto.ErrorResponse;
import soc.wallet.web.dto.UserCreationRequest;
import soc.wallet.web.dto.UserCreationResponse;
import soc.wallet.web.dto.UserFetchResponse;

@Slf4j
public class Controller {

	@SneakyThrows
	private Connection getConnection() {
		String host = Environment.getPostgresHost();
		String port = Environment.getPosgresPort();
		String database = Environment.getPostgresDatabase();
		String username = Environment.getPostgresUser();
		String connectionString = "jdbc:postgresql://%s:%s/%s".formatted(host, port, database);
		return DriverManager.getConnection(connectionString, username, null);
	}

	@SneakyThrows
	@OpenApi(
			summary = "Create user",
			operationId = "createUser",
			path = "/user",
			requestBody = @OpenApiRequestBody(required = true, content = {
					@OpenApiContent(from = UserCreationRequest.class)}),
			methods = HttpMethod.PUT,
			responses = {
					@OpenApiResponse(status = "201", content = {
							@OpenApiContent(from = UserCreationResponse.class)}),
					@OpenApiResponse(status = "400", content = {
							@OpenApiContent(from = ErrorResponse.class)})
			}
	)
	public void createUser(Context ctx) {
		var request = ctx.bodyAsClass(UserCreationRequest.class);
		try (Connection conn = getConnection()) {
			DSL.using(conn, SQLDialect.POSTGRES)
					.transaction(trx -> {
						Long userId = trx.dsl()
								.insertInto(table("users"))
								.columns(field("name"), field("email"))
								.values(request.name(), request.email())
								.onConflictDoNothing()
								.returningResult(field("id"))
								.fetchAnyInto(Long.class);

						if (userId == null) {
							throw new UserAlreadyExistsException();
						}

						ctx.json(new UserCreationResponse(userId,
								DateTimeFormatter.ISO_DATE_TIME.format(OffsetDateTime.now())));
						ctx.status(201);
					});
		}
	}

	@SneakyThrows
	@OpenApi(
			summary = "Fetch user",
			operationId = "retrieveUser",
			path = "/user/{userId}",
			methods = HttpMethod.GET,
			pathParams = {
					@OpenApiParam(name = "userId", type = String.class, description = "ID of the user")
			},
			responses = {
					@OpenApiResponse(status = "200", content = {
							@OpenApiContent(from = UserFetchResponse.class)}),
					@OpenApiResponse(status = "400", content = {
							@OpenApiContent(from = ErrorResponse.class)})
			}
	)
	public void retrieveUser(Context ctx) {
		String requestedId = ctx.pathParam("userId");
		long parsedId = Long.parseLong(requestedId);

		try (Connection conn = getConnection()) {
			UserEntity user = DSL.using(conn, SQLDialect.POSTGRES)
					.select(asterisk())
					.from(table("users"))
					.where(field("id").eq(parsedId))
					.fetchAnyInto(UserEntity.class);

			if (user == null) {
				throw new UserNotFoundException();
			}

			ctx.json(UserFetchResponse.build(user));
			ctx.status(200);
		}
	}

}
