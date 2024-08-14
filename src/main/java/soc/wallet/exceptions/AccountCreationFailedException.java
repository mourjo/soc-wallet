package soc.wallet.exceptions;

import io.javalin.http.HttpStatus;
import soc.wallet.web.dto.ErrorResponse;

public class AccountCreationFailedException extends WalletException {

	@Override
	public ErrorResponse buildResponse() {
		return ErrorResponse.build("Account creation failed");
	}

	public ErrorResponse buildResponse(String message) {
		return ErrorResponse.build("Account creation failed: " + message);
	}

	@Override
	public HttpStatus getStatus() {
		return HttpStatus.BAD_REQUEST;
	}
}
