package exceptions;

import io.javalin.http.HttpStatus;
import web.controller.dto.ErrorResponse;

public abstract class WalletException extends RuntimeException {

	abstract public ErrorResponse buildResponse();
	abstract public HttpStatus getStatus();

	@Override
	public String getMessage() {
		return buildResponse().message();
	}
}
