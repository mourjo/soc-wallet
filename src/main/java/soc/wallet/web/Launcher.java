package soc.wallet.web;

import io.javalin.Javalin;
import lombok.extern.slf4j.Slf4j;
import soc.wallet.common.Environment;
import soc.wallet.web.javalin.ExceptionHandler;
import soc.wallet.web.javalin.OpenAPISetup;

@Slf4j
public class Launcher {

	public static void main(String[] args) {
		buildApp().start(Environment.serverPort());
	}

	public static Javalin buildApp() {
		return Javalin.create(OpenAPISetup::registerPlugins)
				.put("/user", Controller::createUser)
				.get("/user/{userId}", Controller::retrieveUser)
				.exception(Exception.class, ExceptionHandler::handleException);
	}
}
