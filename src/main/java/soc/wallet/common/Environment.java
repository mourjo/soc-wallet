package soc.wallet.common;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Environment {

	private static Environment env = null;
	String postgresHost;
	String posgresPort;
	String postgresUser;
	String postgresDatabase;
	int serverPort;

	private Environment() {
		postgresHost = getEnv("PG_HOST", "localhost");
		posgresPort = getEnv("PG_PORT", "5432");
		postgresUser = getEnv("PG_USER", "justin");
		postgresDatabase = getEnv("PG_DB", "soc_wallet_db");

		String defaultPort = "8818";
		String port = getEnv("SERVER_PORT", defaultPort);
		try {
			serverPort = Integer.parseInt(port);
		} catch (NumberFormatException e) {
			log.warn("Illegal port, falling back to default port {}", defaultPort);
		} finally {
			serverPort = Integer.parseInt(defaultPort);
		}
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

	public static String getPosgresPort() {
		return getInstance().posgresPort;
	}

	public static String getPostgresUser() {
		return getInstance().postgresUser;
	}

	public static String getPostgresDatabase() {
		return getInstance().postgresDatabase;
	}

	public static int getServerPort() {
		return getInstance().serverPort;
	}

	private static String getEnv(String environmentVar, String fallback) {
		String value = Optional.ofNullable(System.getenv(environmentVar)).orElse(fallback);
		log.info("Using environment variable {}={}", environmentVar, value);
		return value;
	}
}
