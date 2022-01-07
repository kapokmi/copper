package com.michelkapoko.copper.bitmex.integration.api;


import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class WithdrawalRequest {

    @NotNull
    private final String apiKey;
    @NotNull
    private final String apiSecret;
    @NotNull
    private final String otpToken;
    @ValidCurrency
    private final Currency currency;
    @NotNull
    private final BigDecimal amount;
    @NotNull
    private final String address;
    private final BigDecimal fee;
    private final String text;

    public WithdrawalRequest(
            String apiKey,
            String apiSecret,
            String otpToken,
            Currency currency,
            BigDecimal amount,
            String address,
            BigDecimal fee,
            String text) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.currency = currency;
        this.otpToken = otpToken;
        this.amount = amount;
        this.address = address;
        this.fee = fee;
        this.text = text;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public String getOtpToken() {
        return otpToken;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getAddress() {
        return address;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public String getText() {
        return text;
    }
}
