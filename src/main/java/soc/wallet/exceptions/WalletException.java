package soc.wallet.exceptions;

import io.javalin.http.HttpStatus;
import soc.wallet.web.dto.ErrorResponse;

public abstract class WalletException extends RuntimeException {
	public WalletException() {
		super();
	}

	public WalletException(String message) {
		super(message);
	}

	abstract public ErrorResponse buildResponse();

	abstract public HttpStatus getStatus();

	@Override
	public String getMessage() {
		return buildResponse().message();
	}
}
