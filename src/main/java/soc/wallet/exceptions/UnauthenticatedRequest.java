package soc.wallet.exceptions;

import io.javalin.http.HttpStatus;
import soc.wallet.web.dto.ErrorResponse;

public class UnauthenticatedRequest extends WalletException {

	@Override
	public ErrorResponse buildResponse() {
		return ErrorResponse.build("Invalid Authentication");
	}

	@Override
	public HttpStatus getStatus() {
		return HttpStatus.UNAUTHORIZED;
	}
}
