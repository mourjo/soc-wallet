package soc.wallet.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.DSL;

@Entity
@Table(name = "users")
public class UserEntity {

	@Id
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

	public static Field<Long> idField() {
		return DSL.field("id", Long.class);
	}

	public static Field<OffsetDateTime> createdAtField() {
		return DSL.field("created_at", OffsetDateTime.class);
	}

	public static Field<String> emailField() {
		return DSL.field("email", String.class);
	}

	public static Field<String> nameField() {
		return DSL.field("name", String.class);
	}

	public static org.jooq.Table<Record> table() {
		return DSL.table("users");
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
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
