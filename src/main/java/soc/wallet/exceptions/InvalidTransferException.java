package soc.wallet.exceptions;

import io.javalin.http.HttpStatus;
import soc.wallet.web.dto.ErrorResponse;

public class InvalidTransferException extends WalletException {

	String message;

	private InvalidTransferException() {
		super();
	}

	public InvalidTransferException(String message) {
		super(message);
		this.message = message;
	}

	@Override
	public ErrorResponse buildResponse() {
		return ErrorResponse.build(message);
	}

	@Override
	public HttpStatus getStatus() {
		return HttpStatus.BAD_REQUEST;
	}
}
