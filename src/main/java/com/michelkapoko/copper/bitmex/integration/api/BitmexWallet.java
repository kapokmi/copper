package com.michelkapoko.copper.bitmex.integration.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Value Object representing the current status of the wallet of the user on the BITMEX exchange.
 */
public class BitmexWallet {
    private final Long account;
    private final Currency currency;
    private final BigDecimal prevDeposited;
    private final BigDecimal prevWithdrawn;
    private final BigDecimal prevTransferIn;
    private final BigDecimal prevTransferOut;
    private final BigDecimal prevAmount;
    private final LocalDateTime prevTimestamp;
    private final BigDecimal deltaDeposited;
    private final BigDecimal deltaWithdrawn;
    private final BigDecimal deltaTransferIn;
    private final BigDecimal deltaTransferOut;
    private final BigDecimal deltaAmount;
    private final BigDecimal deposited;
    private final BigDecimal withdrawn;
    private final BigDecimal transferIn;
    private final BigDecimal transferOut;
    private final BigDecimal amount;
    private final BigDecimal pendingCredit;
    private final BigDecimal pendingDebit;
    private final BigDecimal confirmedDebit;
    private final LocalDateTime timestamp;


    public BitmexWallet(
            Long account,
            Currency currency,
            BigDecimal prevDeposited,
            BigDecimal prevWithdrawn,
            BigDecimal prevTransferIn,
            BigDecimal prevTransferOut,
            BigDecimal prevAmount,
            LocalDateTime prevTimestamp,
            BigDecimal deltaDeposited,
            BigDecimal deltaWithdrawn,
            BigDecimal deltaTransferIn,
            BigDecimal deltaTransferOut,
            BigDecimal deltaAmount,
            BigDecimal deposited,
            BigDecimal withdrawn,
            BigDecimal transferIn,
            BigDecimal transferOut,
            BigDecimal amount,
            BigDecimal pendingCredit,
            BigDecimal pendingDebit,
            BigDecimal confirmedDebit,
            LocalDateTime timestamp) {
        this.account = account;
        this.currency = currency;
        this.prevDeposited = prevDeposited;
        this.prevWithdrawn = prevWithdrawn;
        this.prevTransferIn = prevTransferIn;
        this.prevTransferOut = prevTransferOut;
        this.prevAmount = prevAmount;
        this.prevTimestamp = prevTimestamp;
        this.deltaDeposited = deltaDeposited;
        this.deltaWithdrawn = deltaWithdrawn;
        this.deltaTransferIn = deltaTransferIn;
        this.deltaTransferOut = deltaTransferOut;
        this.deltaAmount = deltaAmount;
        this.deposited = deposited;
        this.withdrawn = withdrawn;
        this.transferIn = transferIn;
        this.transferOut = transferOut;
        this.amount = amount;
        this.pendingCredit = pendingCredit;
        this.pendingDebit = pendingDebit;
        this.confirmedDebit = confirmedDebit;
        this.timestamp = timestamp;
    }

    public Long getAccount() {
        return account;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getPrevDeposited() {
        return prevDeposited;
    }

    public BigDecimal getPrevWithdrawn() {
        return prevWithdrawn;
    }

    public BigDecimal getPrevTransferIn() {
        return prevTransferIn;
    }

    public BigDecimal getPrevTransferOut() {
        return prevTransferOut;
    }

    public BigDecimal getPrevAmount() {
        return prevAmount;
    }

    public LocalDateTime getPrevTimestamp() {
        return prevTimestamp;
    }

    public BigDecimal getDeltaDeposited() {
        return deltaDeposited;
    }

    public BigDecimal getDeltaWithdrawn() {
        return deltaWithdrawn;
    }

    public BigDecimal getDeltaTransferIn() {
        return deltaTransferIn;
    }

    public BigDecimal getDeltaTransferOut() {
        return deltaTransferOut;
    }

    public BigDecimal getDeltaAmount() {
        return deltaAmount;
    }

    public BigDecimal getDeposited() {
        return deposited;
    }

    public BigDecimal getWithdrawn() {
        return withdrawn;
    }

    public BigDecimal getTransferIn() {
        return transferIn;
    }

    public BigDecimal getTransferOut() {
        return transferOut;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getPendingCredit() {
        return pendingCredit;
    }

    public BigDecimal getPendingDebit() {
        return pendingDebit;
    }

    public BigDecimal getConfirmedDebit() {
        return confirmedDebit;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
