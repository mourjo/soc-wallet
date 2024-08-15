package soc.wallet.web;

import static org.jooq.impl.DSL.asterisk;
import static org.jooq.impl.DSL.row;
import static soc.wallet.common.Constants.AUTH_HEADER_NAME;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.format.DateTimeFormatter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jooq.SQLDialect;
import org.jooq.exception.IntegrityConstraintViolationException;
import org.jooq.impl.DSL;
import soc.wallet.common.Environment;
import soc.wallet.entities.AccountEntity;
import soc.wallet.entities.ExternalTransfer;
import soc.wallet.entities.UserEntity;
import soc.wallet.exceptions.AccountCreationFailedException;
import soc.wallet.exceptions.UnauthenticatedRequest;
import soc.wallet.exceptions.UserAlreadyExistsException;
import soc.wallet.exceptions.UserNotFoundException;
import soc.wallet.web.dto.AccountCreationRequest;
import soc.wallet.web.dto.AccountCreationResponse;
import soc.wallet.web.dto.ErrorResponse;
import soc.wallet.web.dto.ExternalTransferCreationRequest;
import soc.wallet.web.dto.ExternalTransferCreationResponse;
import soc.wallet.web.dto.UserCreationRequest;
import soc.wallet.web.dto.UserCreationResponse;
import soc.wallet.web.dto.UserFetchResponse;
import soc.wallet.web.soc.wallet.exceptions.InvalidTransferException;

@Slf4j
public class Controller {

	@SneakyThrows
	private Connection getConnection() {
		String host = Environment.getPostgresHost();
		String port = Environment.getPostgresPort();
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
			headers = {
					@OpenApiParam(name = AUTH_HEADER_NAME, required = true, description = "Authentication Token")},
			responses = {
					@OpenApiResponse(status = "201", content = {
							@OpenApiContent(from = UserCreationResponse.class)}),
					@OpenApiResponse(status = "400", content = {
							@OpenApiContent(from = ErrorResponse.class)}),
					@OpenApiResponse(status = "401", content = {
							@OpenApiContent(from = ErrorResponse.class)})
			}
	)
	public void createUser(Context ctx) {
		if (!Environment.getApiSecret().equals(ctx.header(AUTH_HEADER_NAME))) {
			throw new UnauthenticatedRequest();
		}
		var request = ctx.bodyAsClass(UserCreationRequest.class);
		try (Connection conn = getConnection()) {
			UserEntity user = DSL.using(conn, SQLDialect.POSTGRES)
					.insertInto(UserEntity.table())
					.columns(
							UserEntity.nameField(),
							UserEntity.emailField()
					).values(request.name(), request.email())
					.onConflictDoNothing()
					.returningResult(
							UserEntity.idField(),
							UserEntity.emailField(),
							UserEntity.nameField(),
							UserEntity.createdAtField()
					)
					.fetchAnyInto(UserEntity.class);

			if (user == null) {
				throw new UserAlreadyExistsException();
			}

			ctx.json(new UserCreationResponse(user.getId(),
					DateTimeFormatter.ISO_DATE_TIME.format(user.getCreatedAt())));
			ctx.status(HttpStatus.CREATED);
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
							@OpenApiContent(from = ErrorResponse.class)}),
					@OpenApiResponse(status = "401", content = {
							@OpenApiContent(from = ErrorResponse.class)})
			}
	)
	public void retrieveUser(Context ctx) {
		String requestedId = ctx.pathParam("userId");
		long parsedId = Long.parseLong(requestedId);

		try (Connection conn = getConnection()) {
			UserEntity user = DSL.using(conn, SQLDialect.POSTGRES)
					.select(asterisk())
					.from(UserEntity.table())
					.where(UserEntity.idField().eq(parsedId))
					.fetchAnyInto(UserEntity.class);

			if (user == null) {
				throw new UserNotFoundException();
			}

			if (!Environment.getApiSecret().equals(ctx.header(AUTH_HEADER_NAME))) {
				throw new UnauthenticatedRequest();
			}

			ctx.json(UserFetchResponse.build(user));
			ctx.status(HttpStatus.OK);
		}
	}

	@SneakyThrows
	@OpenApi(
			summary = "Create account",
			operationId = "createAccount",
			path = "/account",
			requestBody = @OpenApiRequestBody(required = true, content = {
					@OpenApiContent(from = AccountCreationRequest.class)}),
			methods = HttpMethod.PUT,
			headers = {
					@OpenApiParam(name = AUTH_HEADER_NAME, required = true, description = "Authentication Token")},
			responses = {
					@OpenApiResponse(status = "201", content = {
							@OpenApiContent(from = AccountCreationResponse.class)}),
					@OpenApiResponse(status = "400", content = {
							@OpenApiContent(from = ErrorResponse.class)}),
					@OpenApiResponse(status = "401", content = {
							@OpenApiContent(from = ErrorResponse.class)})
			}
	)
	public void createAccount(Context ctx) {
		if (!Environment.getApiSecret().equals(ctx.header(AUTH_HEADER_NAME))) {
			throw new UnauthenticatedRequest();
		}
		var request = ctx.bodyAsClass(AccountCreationRequest.class);
		try (Connection conn = getConnection()) {
			try {
				var account = DSL.using(conn, SQLDialect.POSTGRES)
						.insertInto(AccountEntity.table())
						.columns(
								AccountEntity.userIdField(),
								AccountEntity.currencyField(),
								AccountEntity.balanceField()
						).values(
								request.userId(),
								request.currency().toString(),
								new BigDecimal(0)
						).returningResult(
								AccountEntity.userIdField(),
								AccountEntity.balanceField(),
								AccountEntity.currencyField(),
								AccountEntity.createdAtField(),
								AccountEntity.idField()
						)
						.fetchAnyInto(AccountEntity.class);

				var user = DSL.using(conn, SQLDialect.POSTGRES)
						.select(
								UserEntity.createdAtField(),
								UserEntity.idField(),
								UserEntity.emailField(),
								UserEntity.nameField()
						).from(UserEntity.table())
						.where(
								UserEntity.idField().eq(account.getUserId())
						).fetchOneInto(UserEntity.class);
				ctx.json(AccountCreationResponse.build(account, user));

				ctx.status(HttpStatus.CREATED);
			} catch (IntegrityConstraintViolationException ex) {
				throw new AccountCreationFailedException();
			}
		}
	}


	@SneakyThrows
	@OpenApi(
			summary = "External Transfer",
			operationId = "externalTrasnfer",
			path = "/transfer/external",
			requestBody = @OpenApiRequestBody(required = true, content = {
					@OpenApiContent(from = ExternalTransferCreationRequest.class)}),
			methods = HttpMethod.POST,
			headers = {
					@OpenApiParam(name = AUTH_HEADER_NAME, required = true, description = "Authentication Token")},
			responses = {
					@OpenApiResponse(status = "201", content = {
							@OpenApiContent(from = ExternalTransferCreationResponse.class)}),
					@OpenApiResponse(status = "400", content = {
							@OpenApiContent(from = ErrorResponse.class)}),
					@OpenApiResponse(status = "401", content = {
							@OpenApiContent(from = ErrorResponse.class)})
			}
	)
	public void createExternalTransfer(Context ctx) {
		var request = ctx.bodyAsClass(ExternalTransferCreationRequest.class);
		BigDecimal amount = new BigDecimal(request.amount());

		if (!Environment.getApiSecret().equals(ctx.header(AUTH_HEADER_NAME))) {
			throw new UnauthenticatedRequest();
		}

		try (Connection conn = getConnection()) {
			DSL.using(conn, SQLDialect.POSTGRES)
					.transaction(trx -> {
						try {

							AccountEntity account = trx.dsl()
									.select(asterisk())
									.from(AccountEntity.table())
									.where(AccountEntity.idField().eq(request.accountId()))
									.fetchOneInto(AccountEntity.class);

							if (account == null) {
								throw new InvalidTransferException("Account does not exist");
							}

							BigDecimal resultingBalance = account.getBalance().add(amount);
							if (resultingBalance.compareTo(BigDecimal.ZERO) < 0) {
								throw new InvalidTransferException(
										"There is not enough balance to execute this transfer"
								);
							}

							ExternalTransfer transfer = trx.dsl()
									.insertInto(ExternalTransfer.table())
									.columns(
											ExternalTransfer.amountField(),
											ExternalTransfer.sourceField(),
											ExternalTransfer.accountIdField()
									).values(
											amount,
											request.source(),
											account.getId()
									).returningResult(
											ExternalTransfer.idField(),
											ExternalTransfer.accountIdField(),
											ExternalTransfer.createdAtField())
									.fetchOneInto(ExternalTransfer.class);

							int rowsUpdated = trx.dsl().update(AccountEntity.table())
									.set(row(AccountEntity.balanceField()), row(resultingBalance))
									.where(AccountEntity.idField().eq(account.getId()))
									.execute();

							if (transfer == null || rowsUpdated != 1) {
								throw new InvalidTransferException("Failed to create transfer");
							}

							ctx.json(ExternalTransferCreationResponse.build(transfer,
									resultingBalance));
							ctx.status(HttpStatus.CREATED);
						} catch (IntegrityConstraintViolationException ex) {
							throw new AccountCreationFailedException();
						}
					});
		}
	}

}
