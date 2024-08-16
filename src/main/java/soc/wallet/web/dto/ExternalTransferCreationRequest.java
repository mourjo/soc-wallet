package soc.wallet.web.dto;

public record ExternalTransferCreationRequest(long accountId, SupportedCurrency currency, String amount, String source) {

}
