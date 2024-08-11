package web.controller.dto;

import java.time.OffsetDateTime;

public record AccountCreationResponse(
		long id,
		long userId,
		String currency,
		OffsetDateTime creationTime
) {

}
