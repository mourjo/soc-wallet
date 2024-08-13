package soc.wallet.web.dto;

import java.util.Map;
import java.util.Objects;

public record ErrorResponse(String message, Map<String, String> details) {

	public static ErrorResponse build(String message, Map<String, String> details) {
		return new ErrorResponse(message, Objects.requireNonNullElseGet(details, Map::of));
	}

	public static ErrorResponse build(String message) {
		return ErrorResponse.build(message, null);
	}
}
