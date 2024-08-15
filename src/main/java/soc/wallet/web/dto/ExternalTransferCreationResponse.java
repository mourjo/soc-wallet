package soc.wallet.web.dto;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import soc.wallet.entities.ExternalTransfer;

public record ExternalTransferCreationResponse(long id, String balance, long accountId,
											   String createdAt) {

	public static ExternalTransferCreationResponse build(ExternalTransfer entity,
			BigDecimal balance) {
		return new ExternalTransferCreationResponse(
				entity.getId(),
				balance.toPlainString(),
				entity.getAccountId(),
				DateTimeFormatter.ISO_DATE_TIME.format(entity.getCreatedAt())
		);
	}
}
