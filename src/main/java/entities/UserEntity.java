package entities;

import jakarta.persistence.Column;
import java.time.OffsetDateTime;

public class UserEntity {

	long id;

	String name;

	String email;

	@Column(name = "created_at")
	OffsetDateTime createdAt;

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	@Override
	public String toString() {
		return "UserEntity{" +
				"id=" + id +
				", name='" + name + '\'' +
				", email='" + email + '\'' +
				", createdAt=" + createdAt +
				'}';
	}

	public UserEntity(long id, String name, String email, OffsetDateTime createdAt) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.createdAt = createdAt;
	}
}
