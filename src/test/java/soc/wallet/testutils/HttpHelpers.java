package soc.wallet.testutils;

import static soc.wallet.common.Constants.AUTH_HEADER_NAME;

import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import okhttp3.Request.Builder;
import org.jetbrains.annotations.NotNull;
import soc.wallet.common.Environment;
import soc.wallet.entities.AccountEntity;
import soc.wallet.web.dto.ExternalTransferCreationRequest;
import soc.wallet.web.dto.SupportedCurrency;


public class HttpHelpers {
	final static Random r = new Random();

	@NotNull
	public static Consumer<Builder> headers() {
		return req -> req.header(AUTH_HEADER_NAME, Environment.getApiSecret());
	}

	public static ExternalTransferCreationRequest externalTransferRequest(AccountEntity account, SupportedCurrency currency, String amount, String source) {
		return new ExternalTransferCreationRequest(account.getId(), currency, amount, source);
	}

	public static ExternalTransferCreationRequest externalTransferRequest(AccountEntity account, SupportedCurrency currency, String amount) {
		return new ExternalTransferCreationRequest(account.getId(), currency, amount,
				UUID.randomUUID().toString());
	}

	public static ExternalTransferCreationRequest externalTransferRequest(AccountEntity account, SupportedCurrency currency) {
		return new ExternalTransferCreationRequest(account.getId(), currency, Integer.toString(r.nextInt(1000)),
				UUID.randomUUID().toString());
	}

}
