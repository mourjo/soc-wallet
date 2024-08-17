package soc.wallet.integrationtests;

import com.fasterxml.jackson.core.type.TypeReference;
import io.javalin.json.JavalinJackson;
import lombok.SneakyThrows;
import okhttp3.Response;
import soc.wallet.web.dto.AccountCreationResponse;
import soc.wallet.web.dto.AccountFetchResponse;
import soc.wallet.web.dto.ErrorResponse;
import soc.wallet.web.dto.ExternalTransferCreationResponse;
import soc.wallet.web.dto.InternalTransferCreationResponse;
import soc.wallet.web.dto.UserCreationResponse;
import soc.wallet.web.dto.UserFetchResponse;

public class TypeConversion {

	final static JavalinJackson jackson = new JavalinJackson();

	@SneakyThrows
	public static UserCreationResponse toUserCreationResponse(Response response) {
		var typeRef = new TypeReference<UserCreationResponse>() {
		};
		return jackson.fromJsonString(response.body().string(), typeRef.getType());
	}

	@SneakyThrows
	public static AccountCreationResponse toAccountCreationResponse(Response response) {
		var typeRef = new TypeReference<AccountCreationResponse>() {
		};
		return jackson.fromJsonString(response.body().string(), typeRef.getType());
	}

	@SneakyThrows
	public static AccountFetchResponse toAccountFetchResponse(Response response) {
		var typeRef = new TypeReference<AccountFetchResponse>() {
		};
		return jackson.fromJsonString(response.body().string(), typeRef.getType());
	}

	@SneakyThrows
	public static ExternalTransferCreationResponse toExternalTransferCreationResponse(
			Response response) {
		var typeRef = new TypeReference<ExternalTransferCreationResponse>() {
		};
		return jackson.fromJsonString(response.body().string(), typeRef.getType());
	}

	@SneakyThrows
	public static InternalTransferCreationResponse toInternalTransferCreationResponse(
			Response response) {
		var typeRef = new TypeReference<InternalTransferCreationResponse>() {
		};
		return jackson.fromJsonString(response.body().string(), typeRef.getType());
	}

	@SneakyThrows
	public static UserFetchResponse toUserFetchResponse(Response response) {
		var typeRef = new TypeReference<UserFetchResponse>() {
		};
		return jackson.fromJsonString(response.body().string(), typeRef.getType());
	}

	@SneakyThrows
	public static ErrorResponse toErrorResponse(Response response) {
		var typeRef = new TypeReference<ErrorResponse>() {
		};
		return jackson.fromJsonString(response.body().string(), typeRef.getType());
	}

}
