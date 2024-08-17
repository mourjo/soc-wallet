package soc.wallet.web.dto;

import org.jetbrains.annotations.NotNull;



public record TransferInfo(long id, String source, String amount, TransferType transferType, String createdAt) implements Comparable<TransferInfo> {

	@Override
	public int compareTo(@NotNull TransferInfo o) {
		return o.createdAt().compareTo(this.createdAt());
	}

	public enum TransferType {
		EXTERNAL, INTERNAL_DEBIT, INTERNAL_CREDIT
	}
}
