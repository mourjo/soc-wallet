package web.controller;

import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import web.controller.dto.UserCreationRequest;
import web.controller.dto.UserCreationResponse;

public class Controller {

	@OpenApi(
			summary = "Create user",
			operationId = "createUser",
			path = "/user",
			requestBody = @OpenApiRequestBody(required = true, content = {
					@OpenApiContent(from = UserCreationRequest.class)}),
			methods = HttpMethod.PUT,
			responses = {
					@OpenApiResponse(status = "200"),
					@OpenApiResponse(status = "400")
			}
	)
	public static void createUser(Context ctx) {
		ctx.json(new UserCreationResponse(101L,
				DateTimeFormatter.ISO_DATE_TIME.format(OffsetDateTime.now())));
		ctx.status(200);
	}

}
