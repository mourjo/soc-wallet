package soc.wallet.web.dto;

import soc.wallet.entities.UserEntity;
import java.time.format.DateTimeFormatter;

public record UserFetchResponse(long id, String name, String email, String createdAt) {

	public static UserFetchResponse build(UserEntity userEntity) {
		return new UserFetchResponse(userEntity.getId(), userEntity.getName(),
				userEntity.getEmail(),
				DateTimeFormatter.ISO_DATE_TIME.format(userEntity.getCreatedAt()));
	}
}
