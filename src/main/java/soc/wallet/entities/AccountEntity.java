package soc.wallet.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import soc.wallet.web.dto.SupportedCurrency;

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

	@Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
	OffsetDateTime createdAt;

	public AccountEntity() {
	}

	public long getId() {
		return id;
	}

	public long getUserId() {
		return userId;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public String getCurrency() {
		return currency;
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

	public void setId(long id) {
		this.id = id;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}
}
