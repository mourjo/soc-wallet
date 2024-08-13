package soc.wallet.integrationtests;

import com.fasterxml.jackson.core.type.TypeReference;
import io.javalin.json.JavalinJackson;
import lombok.SneakyThrows;
import okhttp3.Response;
import soc.wallet.web.dto.UserCreationResponse;
import soc.wallet.web.dto.UserFetchResponse;

public class TypeConversion {
	final static JavalinJackson jackson = new JavalinJackson();

	@SneakyThrows
	public static UserCreationResponse toUserCreationResponse(Response response) {
		var typeRef = new TypeReference<UserCreationResponse>(){};
		return jackson.fromJsonString(response.body().string(), typeRef.getType());
	}

	@SneakyThrows
	public static UserFetchResponse toUserFetchResponse(Response response) {
		var typeRef = new TypeReference<UserFetchResponse>(){};
		return jackson.fromJsonString(response.body().string(), typeRef.getType());
	}

}
