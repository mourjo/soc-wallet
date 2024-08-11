package web.controller;

import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

	static Javalin app;

	public static void main(String[] args) {

		app = Javalin.create(config -> {
			config.registerPlugin(new OpenApiPlugin(pluginConfig -> {
				pluginConfig.withDefinitionConfiguration((version, definition) -> {
					definition.withInfo(info -> info.setTitle("SoC Wallet"));
				}).withDocumentationPath("/openapi");
			}));

			config.registerPlugin(new SwaggerPlugin(pluginConfig -> {
				pluginConfig.setUiPath("/swagger-ui");
				pluginConfig.setDocumentationPath("/openapi");
			}));

			config.router.apiBuilder(() -> {
				ApiBuilder.path("/user", () -> {
					ApiBuilder.put(Controller::createUser);
				});
			});
		}).exception(Exception.class, (e, ctx) -> {
			log.error("Something wentwrong: {}", e, e);
		});

		app.start(8818);
	}

}
