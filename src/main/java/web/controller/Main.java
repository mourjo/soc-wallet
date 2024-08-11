package web.controller;

import io.javalin.Javalin;
import lombok.extern.slf4j.Slf4j;
import web.controller.javalin.ExceptionHandler;
import web.controller.javalin.OpenAPISetup;

@Slf4j
public class Main {

	static Javalin app;

	public static void main(String[] args) {

		app = Javalin.create(OpenAPISetup::registerPlugins)
				.put("/user", Controller::createUser)
				.get("/user/{userId}", Controller::retrieveUser)
				.exception(Exception.class, ExceptionHandler::handleException);

		app.start(8818);
	}


}
