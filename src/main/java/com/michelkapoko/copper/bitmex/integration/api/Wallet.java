package com.michelkapoko.copper.bitmex.integration.api;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object which is the DTO equivalent of the WalletEntity.
 */
public class Wallet {
    private final Long accountId;
    private final Currency currency;
    private final BigDecimal balance;
    private final BigDecimal reservedFunds;

    public Wallet(
            Long accountId,
            Currency currency,
            BigDecimal balance,
            BigDecimal reservedFunds) {
        this.accountId = accountId;
        this.currency = currency;
        this.balance = balance;
        this.reservedFunds = reservedFunds;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getReservedFunds() {
        return reservedFunds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return Objects.equals(accountId, wallet.accountId)
                && currency == wallet.currency
                && Objects.equals(balance, wallet.balance)
                && Objects.equals(reservedFunds, wallet.reservedFunds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, currency, balance, reservedFunds);
    }
}
