package com.michelkapoko.copper.bitmex.integration.adapter;

import com.michelkapoko.copper.bitmex.integration.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.time.Clock;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Adapter making the direct liaison between our application
 * and bitmex testnet.
 */
@Component
public class BitmexClient  {

    private static final String BITMEX_BASE_USER_API = "/api/v1/user";

    private final String bitmexBaseUrl;
    private final RestTemplate restTemplate;
    private final Clock clock;

    public BitmexClient(
            @Value("${bitmex.integration.base.url:https://testnet.bitmex.com}") String bitmexBaseUrl,
            RestTemplate restTemplate,
            Clock clock) {
        this.bitmexBaseUrl = bitmexBaseUrl;
        this.restTemplate = restTemplate;
        this.clock = clock;
    }

    public List<BitmexWallet> getWallets(WalletRequest request) {
        String url = bitmexBaseUrl + BITMEX_BASE_USER_API + "/wallet?currency=all";
        ResponseEntity<BitmexWallet[]> response = restTemplate.exchange(url, HttpMethod.GET,
                new HttpEntity<>(commonHeaders(url, HttpMethod.GET, request.getApiKey(), request.getApiSecret(), "")),
                BitmexWallet[].class);
        return Arrays.asList(response.getBody());
    }

    public List<Transaction> getWalletHistory(WalletHistoryRequest request) {
        String url = new UriTemplate(bitmexBaseUrl + BITMEX_BASE_USER_API
                + "/walletHistory?currency={currency}&count={count}&start={start}")
                .expand(request.getCurrency(), request.getCount(), request.getStartingPoint()).toString();
        ResponseEntity<Transaction[]> response = restTemplate.exchange(url, HttpMethod.GET,
                new HttpEntity<>(commonHeaders(url, HttpMethod.GET, request.getApiKey(), request.getApiSecret(), "")),
            Transaction[].class);
        return Arrays.asList(response.getBody());
    }

    public Transaction requestWithdrawal(WithdrawalRequest request) {
        String body = buildWithdrawalRequestBody(request);
        String url = bitmexBaseUrl + BITMEX_BASE_USER_API + "/requestWithdrawal";
        HttpHeaders headers = commonHeaders(url, HttpMethod.POST, request.getApiKey(), request.getApiSecret(), body);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        ResponseEntity<Transaction> response = restTemplate.exchange(url, HttpMethod.POST,
                new HttpEntity(body, headers), Transaction.class);
        return response.getBody();
    }

    private HttpHeaders commonHeaders(
            String url,
            HttpMethod requestMethod,
            String apiKey,
            String apiSecret,
            String requestBody) {

        // expiringTime  = currentTime + 5s to give us some seconds of grace in addition to the time
        // it takes for the request to get to BITMEX's server.
        Long expiringTime = (clock.millis() / 1000) + 6;

        HttpHeaders headers = new HttpHeaders();
        headers.set("api-expires", expiringTime.toString());
        headers.set("api-key", apiKey);
        headers.set("api-signature", BitmexIntegrationRequestUtil.generateSignature(
                apiSecret, requestMethod, url.substring(bitmexBaseUrl.length()), expiringTime, requestBody));
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private String buildWithdrawalRequestBody(WithdrawalRequest command) {
        StringBuilder builder = new StringBuilder();
        builder.append("address=");
        builder.append(command.getAddress());
        builder.append("&");
        builder.append("amount=");
        builder.append(command.getAmount());
        builder.append("&");
        builder.append("currency=");
        builder.append(command.getCurrency());
        builder.append("&");
        builder.append("otpToken=");
        builder.append(command.getOtpToken());
        builder.append("&");
        if (command.getFee() != null) {
            builder.append("fee=");
            builder.append(command.getFee());
            builder.append("&");
        }
        if (command.getText() != null) {
            builder.append("text=");
            builder.append(command.getText());
            builder.append("&");
        }
        return builder.substring(0, builder.length() - 1);
    }
}
