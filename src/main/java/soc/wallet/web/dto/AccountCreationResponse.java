package soc.wallet.web.dto;

public record AccountCreationResponse(
		long id,
		long userId,
		String currency,
		String userEmail,
		String creationTime
) {

}
