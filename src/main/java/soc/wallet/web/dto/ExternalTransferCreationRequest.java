package soc.wallet.web.dto;

public record ExternalTransferCreationRequest(long accountId, String amount, String source) {

}
