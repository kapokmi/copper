package com.michelkapoko.copper.bitmex.integration.domain;

import com.michelkapoko.copper.bitmex.integration.api.Currency;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(
        schema = "bitmex",
        name = "wallets",
        uniqueConstraints = {@UniqueConstraint(name = "unique_currency_wallet", columnNames = {"accountId", "currency"})})
public class WalletEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_id_seq_gen")
    @SequenceGenerator(
            name = "wallet_id_seq_gen",
            sequenceName = "wallet_id_seq",
            allocationSize = 1
    )
    private Long walletId;
    private Long accountId;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private BigDecimal balance;
    private BigDecimal reservedFunds;
    private Instant created;
    private Instant lastUpdated;

    public WalletEntity() {}

    public WalletEntity(
            Long accountId,
            Currency currency,
            BigDecimal balance,
            BigDecimal reservedFunds,
            Instant created,
            Instant lastUpdated) {
        this.accountId = accountId;
        this.currency = currency;
        this.balance = balance;
        this.reservedFunds = reservedFunds;
        this.created = created;
        this.lastUpdated = lastUpdated;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public void setLastUpdated(Instant updateTime){
        this.lastUpdated = updateTime;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setReservedFunds(BigDecimal reservedFunds) {
        this.reservedFunds = reservedFunds;
    }

    public Long getWalletId() {
        return walletId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WalletEntity entity = (WalletEntity) o;
        return Objects.equals(walletId, entity.walletId)
                && Objects.equals(accountId, entity.accountId)
                && currency == entity.currency
                && Objects.equals(balance, entity.balance)
                && Objects.equals(reservedFunds, entity.reservedFunds)
                && Objects.equals(created, entity.created)
                && Objects.equals(lastUpdated, entity.lastUpdated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(walletId, accountId, currency, balance, reservedFunds, created, lastUpdated);
    }
}
