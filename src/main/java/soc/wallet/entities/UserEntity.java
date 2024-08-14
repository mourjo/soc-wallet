package soc.wallet.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
public class UserEntity {

	@Column(name = "id")
	long id;

	@Column(name = "name")
	String name;

	@Column(name = "email")
	String email;

	@Column(name = "created_at")
	OffsetDateTime createdAt;

	public UserEntity() {

	}

	public UserEntity(long id, String name, String email, OffsetDateTime createdAt) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.createdAt = createdAt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
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
}
