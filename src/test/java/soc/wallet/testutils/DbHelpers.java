package soc.wallet.testutils;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.SneakyThrows;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import soc.wallet.common.Environment;
import soc.wallet.entities.AccountEntity;
import soc.wallet.entities.UserEntity;

public class DbHelpers {

	static final AtomicInteger counter = new AtomicInteger(0);


	@SneakyThrows
	public static Connection getConnection() {
		String host = Environment.getPostgresHost();
		String port = Environment.getPostgresPort();
		String database = Environment.getPostgresDatabase();
		String username = Environment.getPostgresUser();
		String connectionString = "jdbc:postgresql://%s:%s/%s".formatted(host, port, database);
		return DriverManager.getConnection(connectionString, username, null);
	}

	@SneakyThrows
	public static UserEntity insertUser(String prefix) {
		int c = counter.incrementAndGet();
		String email = prefix + "-" + c + "-" + UUID.randomUUID() + "@gmail.com";
		String name = prefix + "-" + c;

		try (Connection conn = getConnection()) {
			return DSL.using(conn, SQLDialect.POSTGRES)
					.insertInto(UserEntity.table())
					.columns(UserEntity.nameField(), UserEntity.emailField())
					.values(name, email)
					.returningResult(UserEntity.idField(), UserEntity.emailField())
					.fetchAnyInto(UserEntity.class);
		}
	}

	@SneakyThrows
	public static AccountEntity insertAccount(String currency, long userId) {
		try (Connection conn = getConnection()) {
			return DSL.using(conn, SQLDialect.POSTGRES)
					.insertInto(AccountEntity.table())
					.columns(AccountEntity.currencyField(), AccountEntity.userIdField(),
							AccountEntity.balanceField())
					.values(currency, userId, BigDecimal.ZERO)
					.returningResult(AccountEntity.balanceField(), AccountEntity.idField(),
							AccountEntity.createdAtField(), AccountEntity.userIdField(),
							AccountEntity.currencyField())
					.fetchAnyInto(AccountEntity.class);
		}
	}

	@SneakyThrows
	public static AccountEntity getAccount(long accountId) {
		try (Connection conn = getConnection()) {
			return DSL.using(conn, SQLDialect.POSTGRES)
					.select(AccountEntity.balanceField(), AccountEntity.idField(),
							AccountEntity.createdAtField(), AccountEntity.userIdField(),
							AccountEntity.currencyField())
					.from(AccountEntity.table())
					.where(AccountEntity.idField().eq(accountId))
					.fetchAnyInto(AccountEntity.class);
		}
	}

}
