package soc.wallet.exceptions;

import io.javalin.http.HttpStatus;
import soc.wallet.web.dto.ErrorResponse;

public class UserAlreadyExistsException extends WalletException {

    @Override
    public ErrorResponse buildResponse() {
        return ErrorResponse.build("User already exists");
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }
}
