package soc.wallet.web.dto;

import java.time.OffsetDateTime;

public record AccountCreationResponse(
		long id,
		long userId,
		String currency,
		String userEmail,
		String creationTime
) {

}
