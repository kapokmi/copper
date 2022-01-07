package com.michelkapoko.copper.bitmex.integration.api;


import javax.validation.constraints.NotNull;

/**
 * Value object representing the request to get the wallet of a specific user
 */
public class WalletRequest {

    @NotNull
    private final String apiKey;
    @NotNull
    private final String apiSecret;

    public WalletRequest(
            String apiKey,
            String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }
}
