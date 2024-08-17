package soc.wallet.web.dto;

public record InternalTransferCreationRequest(long sourceAccount,
											  long destinationAccount,
											  SupportedCurrency currency,
											  String amount) {

}
