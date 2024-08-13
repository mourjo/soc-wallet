package soc.wallet.web.javalin;

import soc.wallet.exceptions.WalletException;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import soc.wallet.web.dto.ErrorResponse;

@Slf4j
public class ExceptionHandler {

	public static void handleException(Exception e, Context context) {
		switch (e) {
			case WalletException exp -> handleWalletException(exp, context);
			case NumberFormatException nfe -> handleNumberFormatExceptionException(nfe, context);
			default -> handleGenericException(e, context);
		}
	}

	private static void handleGenericException(Exception e, Context context) {
		log.error("Error: {}", e.getMessage(), e);
		context.json(ErrorResponse.build(e.getMessage(),
				Map.of("exception_class", e.getClass().toString())));
		context.status(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private static void handleWalletException(WalletException e, Context context) {
		context.json(e.buildResponse());
		context.status(e.getStatus());
	}

	private static void handleNumberFormatExceptionException(NumberFormatException e,
			Context context) {
		context.json(ErrorResponse.build("Invalid number in request"));
		context.status(HttpStatus.BAD_REQUEST);
	}
}
