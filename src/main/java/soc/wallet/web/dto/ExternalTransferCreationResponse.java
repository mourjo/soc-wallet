package soc.wallet.web.dto;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import soc.wallet.entities.ExternalTransfer;

public record ExternalTransferCreationResponse(long id, String balance, long accountId,
                                               SupportedCurrency currency, String createdAt) {

    public static ExternalTransferCreationResponse build(ExternalTransfer entity,
        BigDecimal balance, String currency) {
        return new ExternalTransferCreationResponse(
            entity.getId(),
            balance.toPlainString(),
            entity.getAccountId(),
            SupportedCurrency.valueOf(currency),
            DateTimeFormatter.ISO_DATE_TIME.format(entity.getCreatedAt())
        );
    }
}
