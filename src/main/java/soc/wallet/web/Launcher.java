package soc.wallet.web;

import io.javalin.Javalin;
import lombok.extern.slf4j.Slf4j;
import soc.wallet.common.Environment;
import soc.wallet.web.javalin.ExceptionHandler;
import soc.wallet.web.javalin.OpenAPISetup;

@Slf4j
public class Launcher {

	public static void main(String[] args) {
		buildApp().start(Environment.getServerPort());
	}

	public static Javalin buildApp() {
		final Controller controller = new Controller();

		return Javalin.create(OpenAPISetup::registerPlugins)
				.put("/user", controller::createUser)
				.get("/user/{userId}", controller::retrieveUser)
				.put("/account", controller::createAccount)
				.exception(Exception.class, ExceptionHandler::handleException);
	}
}
