package soc.wallet.testutils;

import static soc.wallet.common.Constants.AUTH_HEADER_NAME;

import io.javalin.testtools.HttpClient;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import okhttp3.Request.Builder;
import org.jetbrains.annotations.NotNull;
import soc.wallet.common.Environment;
import soc.wallet.entities.AccountEntity;
import soc.wallet.web.dto.ExternalTransferCreationRequest;
import soc.wallet.web.dto.InternalTransferCreationRequest;
import soc.wallet.web.dto.SupportedCurrency;


public class HttpHelpers {

	final static Random r = new Random();

	@NotNull
	public static Consumer<Builder> headers() {
		return req -> req.header(AUTH_HEADER_NAME, Environment.getApiSecret());
	}

	public static ExternalTransferCreationRequest externalTransferRequest(
			AccountEntity account,
			String amount) {
		return new ExternalTransferCreationRequest(
				account.getId(),
				SupportedCurrency.valueOf(account.getCurrency()),
				amount,
				UUID.randomUUID().toString()
		);
	}

	public static ExternalTransferCreationRequest externalTransferRequest(
			long sourceAccount,
			String currency,
			String amount) {
		return new ExternalTransferCreationRequest(
				sourceAccount,
				SupportedCurrency.valueOf(currency),
				amount,
				UUID.randomUUID().toString()
		);
	}


	public static InternalTransferCreationRequest internalTransferRequest(
			AccountEntity source,
			AccountEntity destination,
			String amount) {
		return new InternalTransferCreationRequest(
				source.getId(),
				destination.getId(),
				SupportedCurrency.valueOf(source.getCurrency()),
				amount
		);
	}

	public static InternalTransferCreationRequest internalTransferRequest(
			long sourceAccount,
			long destinationAccount,
			String currency,
			String amount) {
		return new InternalTransferCreationRequest(
				sourceAccount,
				destinationAccount,
				SupportedCurrency.valueOf(currency),
				amount
		);
	}

	public static void externalTransfer(HttpClient client, AccountEntity account, String amount) {
		client.post(
				"/transfer/external",
				externalTransferRequest(account, amount),
				headers()
		);
	}

	public static void internalTransfer(HttpClient client, AccountEntity source,
			AccountEntity destination, String amount) {
		client.post(
				"/transfer/internal",
				internalTransferRequest(source, destination, amount),
				headers()
		);
	}
}
