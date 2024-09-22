package soc.wallet.web.dto;

import java.time.format.DateTimeFormatter;
import soc.wallet.entities.AccountEntity;
import soc.wallet.entities.UserEntity;

public record AccountCreationResponse(
    long id,
    long userId,
    String currency,
    String balance,
    String userEmail,
    String creationTime
) {

    public static AccountCreationResponse build(AccountEntity account, UserEntity user) {
        return new AccountCreationResponse(
            account.getId(),
            account.getUserId(),
            account.getCurrency(),
            account.getBalance().toPlainString(),
            user.getEmail(),
            DateTimeFormatter.ISO_DATE_TIME.format(account.getCreatedAt()));
    }
}
