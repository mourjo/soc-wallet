package soc.wallet.common;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Environment {

	private static Environment env = null;
	private final String postgresHost;
	private final String postgresPort;
	private final String postgresUser;
	private final String postgresDatabase;
	private final int serverPort;
	private final String apiSecret;

	private Environment() {
		postgresHost = getEnv("PG_HOST", "localhost");
		postgresPort = getEnv("PG_PORT", "5432");
		postgresUser = getEnv("PG_USER", "justin");
		postgresDatabase = getEnv("PG_DB", "soc_wallet_db");
		apiSecret = getEnv("API_SECRET", "noauth");

		String defaultPort = "8818";
		int port;
		try {
			port = Integer.parseInt(getEnv("SERVER_PORT", defaultPort));
		} catch (NumberFormatException e) {
			log.warn("Illegal port, falling back to default port {}", defaultPort);
			port = Integer.parseInt(defaultPort);
		} finally {
		}
		serverPort = port;
	}

	public static Environment getInstance() {
		if (env == null) {
			env = new Environment();
		}
		return env;
	}

	public static String getPostgresHost() {
		return getInstance().postgresHost;
	}

	public static String getPostgresPort() {
		return getInstance().postgresPort;
	}

	public static String getPostgresUser() {
		return getInstance().postgresUser;
	}

	public static String getPostgresDatabase() {
		return getInstance().postgresDatabase;
	}

	public static String getApiSecret() {
		return getInstance().postgresDatabase;
	}

	public static int getServerPort() {
		return getInstance().serverPort;
	}

	private static String getEnv(String environmentVar, String fallback) {
		String value = Optional.ofNullable(System.getenv(environmentVar)).orElse(fallback);
		log.debug("Using environment variable {}={}", environmentVar, value);
		return value;
	}
}
