package soc.wallet.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.jooq.Field;
import org.jooq.impl.DSL;

@Table(name = "internal_transfers")
public class InternalTransfer {

    @Id
    @Column(name = "id")
    long id;

    @Column(name = "source")
    long sourceAccountId;

    @Column(name = "destination")
    long destinationAccountId;

    @Column(name = "amount")
    BigDecimal amount;

    @Column(name = "created_at")
    OffsetDateTime createdAt;

    public InternalTransfer() {
    }

    public static Field<Long> idField() {
        return DSL.field("id", Long.class);
    }

    public static Field<BigDecimal> amountField() {
        return DSL.field("amount", BigDecimal.class);
    }

    public static Field<Long> sourceAccountIdField() {
        return DSL.field("source", Long.class);
    }

    public static Field<Long> destinationAccountIdField() {
        return DSL.field("destination", Long.class);
    }

    public static Field<OffsetDateTime> createdAtField() {
        return DSL.field("created_at", OffsetDateTime.class);
    }

    public static org.jooq.Table<org.jooq.Record> table() {
        return DSL.table("internal_transfers");
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(long sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public long getDestinationAccountId() {
        return destinationAccountId;
    }

    public void setDestinationAccountId(long destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
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
