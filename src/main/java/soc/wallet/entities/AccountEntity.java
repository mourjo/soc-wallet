package soc.wallet.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.DSL;

@Entity
@Table(name = "accounts")
public class AccountEntity {

	@Column(name = "id")
	long id;

	@Column(name = "user_id")
	long userId;

	@Column(name = "balance")
	BigDecimal balance;

	@Column(name = "currency")
	String currency;

	@Column(name = "created_at")
	OffsetDateTime createdAt;

	public AccountEntity() {
	}

	public static Field<Long> idField() {
		return DSL.field("id", Long.class);
	}

	public static Field<String> currencyField() {
		return DSL.field("currency", String.class);
	}

	public static Field<Long> userIdField() {
		return DSL.field("user_id", Long.class);
	}

	public static Field<OffsetDateTime> createdAtField() {
		return DSL.field("created_at", OffsetDateTime.class);
	}

	public static Field<BigDecimal> balanceField() {
		return DSL.field("balance", BigDecimal.class);
	}

	public static org.jooq.Table<Record> table() {
		return DSL.table("accounts");
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {
		return "AccountEntity{" +
				"id=" + id +
				", userId='" + userId + '\'' +
				", balance=" + balance +
				", currency=" + currency +
				", createdAt=" + createdAt +
				'}';
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
