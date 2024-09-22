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
            .post("/user", controller::createUser)
            .get("/user/{userId}", controller::retrieveUser)
            .get("/account/{accountId}", controller::retrieveAccount)
            .post("/account", controller::createAccount)
            .post("/transfer/external", controller::createExternalTransfer)
            .post("/transfer/internal", controller::createInternalTransfer)
            .exception(Exception.class, ExceptionHandler::handleException);
    }
}
