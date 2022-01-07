package com.michelkapoko.copper.bitmex.integration.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Value Object representing a transaction which will be sent back as part of the wallet history.
 */
public class Transaction {
    private final String transactID;
    private final Long account;
    private final Currency currency;
    private final TransactType transactType;
    private final BigDecimal amount;
    private final BigDecimal fee;
    private final TransactStatus transactStatus;
    private final String address;
    private final String tx;
    private final String text;
    private final LocalDateTime transactTime;
    private final LocalDateTime timestamp;
    private final BigDecimal walletBalance;
    private final BigDecimal marginBalance;

    public Transaction(
            String transactID,
            Long account,
            Currency currency,
            TransactType transactType,
            BigDecimal amount,
            BigDecimal fee,
            TransactStatus transactStatus,
            String address, String tx,
            String text,
            LocalDateTime transactTime,
            LocalDateTime timestamp,
            BigDecimal walletBalance,
            BigDecimal marginBalance) {
        this.transactID = transactID;
        this.account = account;
        this.currency = currency;
        this.transactType = transactType;
        this.amount = amount;
        this.fee = fee;
        this.transactStatus = transactStatus;
        this.address = address;
        this.tx = tx;
        this.text = text;
        this.transactTime = transactTime;
        this.timestamp = timestamp;
        this.walletBalance = walletBalance;
        this.marginBalance = marginBalance;
    }


    public String getTransactID() {
        return transactID;
    }

    public Long getAccount() {
        return account;
    }

    public Currency getCurrency() {
        return currency;
    }

    public TransactType getTransactType() {
        return transactType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public TransactStatus getTransactStatus() {
        return transactStatus;
    }

    public String getAddress() {
        return address;
    }

    public String getTx() {
        return tx;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getTransactTime() {
        return transactTime;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public BigDecimal getWalletBalance() {
        return walletBalance;
    }

    public BigDecimal getMarginBalance() {
        return marginBalance;
    }
}
