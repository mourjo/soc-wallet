package soc.wallet.web.dto;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import soc.wallet.entities.InternalTransfer;

public record InternalTransferCreationResponse(long id,
                                               String destinationBalance,
                                               String sourceBalance,
                                               long sourceAccountId,
                                               long destinationAccountId,
                                               SupportedCurrency currency,
                                               String createdAt) {

    public static InternalTransferCreationResponse build(InternalTransfer entity,
        BigDecimal sourceBalance, BigDecimal destinationBalance, String currency) {
        return new InternalTransferCreationResponse(
            entity.getId(),
            destinationBalance.toPlainString(),
            sourceBalance.toPlainString(),
            entity.getSourceAccountId(),
            entity.getDestinationAccountId(),
            SupportedCurrency.valueOf(currency),
            DateTimeFormatter.ISO_DATE_TIME.format(entity.getCreatedAt())
        );
    }
}
