package exceptions;

import io.javalin.http.HttpStatus;
import web.controller.dto.ErrorResponse;

public class UserNotFoundException extends WalletException {

	@Override
	public ErrorResponse buildResponse() {
		return ErrorResponse.build("User does not exist");
	}

	@Override
	public HttpStatus getStatus() {
		return HttpStatus.NOT_FOUND;
	}
}
