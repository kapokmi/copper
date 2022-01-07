package com.michelkapoko.copper.bitmex.integration.api;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class WalletHistoryRequest {

    private static final int DEFAULT_COUNT = 100;

    @NotNull
    private final String apiKey;
    @NotNull
    private final String apiSecret;
    private final Currency currency;
    @Min(0) @Max(500)
    private final Integer count;
    @Min(0)
    private final Integer startingPoint;

    public WalletHistoryRequest(
            String apiKey,
            String apiSecret,
            Currency currency,
            Integer count,
            Integer startingPoint) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.currency = currency == null ? Currency.XBt : currency;
        this.count = count == null ? DEFAULT_COUNT : count;
        this.startingPoint = startingPoint == null ? 0 : startingPoint;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Integer getCount() {
        return count;
    }

    public Integer getStartingPoint() {
        return startingPoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WalletHistoryRequest that = (WalletHistoryRequest) o;
        return Objects.equals(apiKey, that.apiKey)
                && Objects.equals(apiSecret, that.apiSecret)
                && currency == that.currency
                && Objects.equals(count, that.count)
                && Objects.equals(startingPoint, that.startingPoint);
    }
}
