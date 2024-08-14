package soc.wallet.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.jooq.Field;
import org.jooq.impl.DSL;

@Table(name = "external_transfers")
public class ExternalTransfer {

	@Column(name = "id")
	long id;

	@Column(name = "account")
	long accountId;
	@Column(name = "source")
	String source;
	@Column(name = "amount")
	BigDecimal amount;
	@Column(name = "created_at")
	OffsetDateTime createdAt;

	public ExternalTransfer() {
	}

	public static Field<Long> idField() {
		return DSL.field("id", Long.class);
	}

	public static Field<BigDecimal> amountField() {
		return DSL.field("amount", BigDecimal.class);
	}

	public static Field<Long> accountIdField() {
		return DSL.field("account", Long.class);
	}

	public static Field<OffsetDateTime> createdAtField() {
		return DSL.field("created_at", OffsetDateTime.class);
	}

	public static Field<String> sourceField() {
		return DSL.field("source", String.class);
	}

	public static org.jooq.Table<org.jooq.Record> table() {
		return DSL.table("accounts");
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}

}
