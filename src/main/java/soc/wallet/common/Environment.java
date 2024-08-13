package soc.wallet.common;

import java.util.Optional;

public class Environment {

	public static String postgresHost() {
		return getEnv("PG_HOST").orElse("localhost");
	}

	public static String postgresPort() {
		return getEnv("PG_PORT").orElse("5432");
	}

	public static String postgresUser() {
		return getEnv("PG_USER").orElse("justin");
	}

	public static String postgresDatabase() {
		return getEnv("PG_DB").orElse("soc_wallet_db");
	}

	public static int serverPort() {
		return getEnv("SERVER_PORT")
				.map(s -> {
					try {
						return Integer.parseInt(s);
					} catch (NumberFormatException e) {
						return null;
					}
				})
				.orElse(8818);
	}

	private static Optional<String> getEnv(String environmentVar) {
		return Optional.ofNullable(System.getenv(environmentVar));
	}
}
