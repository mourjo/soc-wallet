package soc.wallet.web.dto;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import soc.wallet.entities.AccountEntity;
import soc.wallet.entities.ExternalTransfer;
import soc.wallet.entities.InternalTransfer;
import soc.wallet.entities.UserEntity;

public record AccountFetchResponse(long id, String balance, String currency, long userId,  String userEmail, String createdAt,
								   List<TransferInfo> transfers) {

	public static AccountFetchResponse build(
			AccountEntity account,
			UserEntity user,
			List<ExternalTransfer> externalTransfers,
			List<InternalTransfer> internalTransfers
	) {

		var externalTransferInfo = externalTransfers.stream()
				.map(
						tr -> new TransferInfo(
								tr.getId(),
								tr.getSource(),
								tr.getAmount().toPlainString(),
								TransferType.EXTERNAL,
								DateTimeFormatter.ISO_DATE_TIME.format(tr.getCreatedAt())
						)
				).toList();

		var internalTransferInfo = internalTransfers.stream()
				.map(
						tr -> new TransferInfo(
								tr.getId(),
								Long.toString(tr.getSourceAccountId()),
								tr.getAmount().toPlainString(),
								TransferType.INTERNAL,
								DateTimeFormatter.ISO_DATE_TIME.format(tr.getCreatedAt())
						)
				).toList();

		var allTransfers = new ArrayList<>(externalTransferInfo);
		allTransfers.addAll(internalTransferInfo);
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
