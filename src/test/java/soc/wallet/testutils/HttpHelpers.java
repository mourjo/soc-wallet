package soc.wallet.testutils;

import static soc.wallet.common.Constants.AUTH_HEADER_NAME;

import java.util.function.Consumer;
import okhttp3.Request.Builder;
import org.jetbrains.annotations.NotNull;
import soc.wallet.common.Environment;


public class HttpHelpers {

	@NotNull
	public static Consumer<Builder> headers() {
		return req -> req.header(AUTH_HEADER_NAME, Environment.getApiSecret());
	}

}
