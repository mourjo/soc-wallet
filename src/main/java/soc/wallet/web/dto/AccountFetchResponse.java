package soc.wallet.web.dto;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import soc.wallet.entities.AccountEntity;
import soc.wallet.entities.ExternalTransfer;
import soc.wallet.entities.InternalTransfer;
import soc.wallet.entities.UserEntity;
import soc.wallet.web.dto.TransferInfo.TransferType;

public record AccountFetchResponse(long id, String balance, String currency, long userId,
								   String userEmail, String createdAt,
								   List<TransferInfo> transfers) {

	public static AccountFetchResponse build(
			AccountEntity account,
			UserEntity user,
			List<ExternalTransfer> externalTransfers,
			List<InternalTransfer> internalCreditTransfers,
			List<InternalTransfer> internalDebitTransfers
	) {

		var externalTransferInfo = externalTransfers.stream()
				.map(
						tr -> new TransferInfo(
								tr.getId(),
								tr.getSource(),
								tr.getAmount().toPlainString(),
								tr.getAmount().compareTo(BigDecimal.ZERO) < 0
										? TransferType.EXTERNAL_DEBIT
										: TransferType.EXTERNAL_CREDIT,
								DateTimeFormatter.ISO_DATE_TIME.format(tr.getCreatedAt())
						)
				).toList();

		var internalCreditTransferInfo = internalCreditTransfers.stream()
				.map(
						tr -> new TransferInfo(
								tr.getId(),
								Long.toString(tr.getSourceAccountId()),
								tr.getAmount().toPlainString(),
								TransferType.INTERNAL_CREDIT,
								DateTimeFormatter.ISO_DATE_TIME.format(tr.getCreatedAt())
						)
				).toList();

		var internalDebitTransferInfo = internalDebitTransfers.stream()
				.map(
						tr -> new TransferInfo(
								tr.getId(),
								Long.toString(tr.getDestinationAccountId()),
								"-" + tr.getAmount().toPlainString(),
								TransferType.INTERNAL_DEBIT,
								DateTimeFormatter.ISO_DATE_TIME.format(tr.getCreatedAt())
						)
				).toList();

		var allTransfers = new ArrayList<>(externalTransferInfo);
		allTransfers.addAll(internalCreditTransferInfo);
		allTransfers.addAll(internalDebitTransferInfo);
		Collections.sort(allTransfers);

		return new AccountFetchResponse(
				account.getId(),
				account.getBalance().toPlainString(),
				account.getCurrency(),
				account.getUserId(),
				user.getEmail(),
				DateTimeFormatter.ISO_DATE_TIME.format(account.getCreatedAt()),
				allTransfers
		);
	}

}
