package com.michelkapoko.copper.bitmex.integration.adapter;

import com.michelkapoko.copper.bitmex.integration.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.stream.Stream;

import static com.michelkapoko.copper.bitmex.integration.api.Currency.USDt;
import static com.michelkapoko.copper.bitmex.integration.api.Currency.XBt;
import static com.michelkapoko.copper.bitmex.integration.api.TransactType.WITHDRAWAL;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BitmexClientTest {

    private static final String BITMEX_BASE_URL = "https://testnet.bitmex.com";
    private static final String API_KEY = "qVYl9mK6Usy5BMsnX971R3er";
    private static final String API_SECRET = "chNOOS4KvNXR_Xq4k4c9qsfoKWvnDecLATCRlcBwyKDYnWgO";

    @Mock
    private RestTemplate restTemplate;
    private Clock clock;
    private BitmexClient underTest;

    @BeforeEach
    void setup() {
        clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"));
        underTest = new BitmexClient(BITMEX_BASE_URL, restTemplate, clock);
    }

    @Test
    void shouldCallBitmexApiWithCorrectParametersWhenGettingAWallet(){
        ResponseEntity<BitmexWallet[]> response = new ResponseEntity<BitmexWallet[]>(new BitmexWallet[1], null, HttpStatus.OK);
        String url = "https://testnet.bitmex.com/api/v1/user/wallet?currency=all";
        HttpEntity expectedHttpEntity = new HttpEntity(commonHeaders(url, HttpMethod.GET, ""));
        when(restTemplate.exchange(
                anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(HttpEntity.class),
                eq(BitmexWallet[].class))).thenReturn(response);
        underTest.getWallets(new WalletRequest(API_KEY, API_SECRET));
        verify(restTemplate).exchange(eq(url), eq(HttpMethod.GET), eq(expectedHttpEntity), eq(BitmexWallet[].class));
    }

    @Test
    void shouldCallBitmexApiWithCorrectParametersWhenGettingAWalletHistory(){
        ResponseEntity<Transaction[]> response = new ResponseEntity<Transaction[]>(new Transaction[1], null, HttpStatus.OK);
        String url = "https://testnet.bitmex.com/api/v1/user/walletHistory?currency=USDt&count=100&start=0";
        HttpEntity expectedHttpEntity = new HttpEntity(commonHeaders(url, HttpMethod.GET, ""));
        when(restTemplate.exchange(
                anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(HttpEntity.class),
                eq(Transaction[].class))).thenReturn(response);
        underTest.getWalletHistory(new WalletHistoryRequest(API_KEY, API_SECRET, USDt, 100, 0));
        verify(restTemplate).exchange(eq(url), eq(HttpMethod.GET), eq(expectedHttpEntity), eq(Transaction[].class));
    }

    @ParameterizedTest
    @MethodSource("provideWithdrawalRequestArguments")
    void shouldCallBitmexApiWithCorrectParametersWhenRequestingAWithdrawal(WithdrawalRequest request, String requestBody){
        Transaction dummyTransaction = new Transaction(
                "transactID",
                1L,
                XBt,
                WITHDRAWAL,
                BigDecimal.valueOf(1000L),
                BigDecimal.ZERO,
                TransactStatus.CONFIRMED,
                "address",
                "tx",
                "text",
                LocalDateTime.now(clock),
                LocalDateTime.now(clock),
                BigDecimal.valueOf(1000L),
                BigDecimal.ZERO
        );

        ResponseEntity<Transaction> response = new ResponseEntity<Transaction>(dummyTransaction, null, HttpStatus.OK);
        String url = "https://testnet.bitmex.com/api/v1/user/requestWithdrawal";
        HttpHeaders expectedRequestHeaders = commonHeaders(url, HttpMethod.POST, requestBody);
        expectedRequestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity expectedHttpEntity = new HttpEntity(requestBody, expectedRequestHeaders);
        when(restTemplate.exchange(
                anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(HttpEntity.class),
                eq(Transaction.class))).thenReturn(response);
        underTest.requestWithdrawal(request);
        verify(restTemplate).exchange(eq(url), eq(HttpMethod.POST), eq(expectedHttpEntity), eq(Transaction.class));
    }

    private HttpHeaders commonHeaders(
            String url,
            HttpMethod requestMethod,
            String requestBody) {

        Long expiringTime = (clock.millis() / 1000) + 6;

        HttpHeaders headers = new HttpHeaders();
        headers.set("api-expires", expiringTime.toString());
        headers.set("api-key", API_KEY);
        headers.set("api-signature", BitmexIntegrationRequestUtil.generateSignature(
                API_SECRET, requestMethod, url.substring(BITMEX_BASE_URL.length()), expiringTime, requestBody));
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static Stream<Arguments> provideWithdrawalRequestArguments() {
        WithdrawalRequest requestWithNeitherFeeNorText = new WithdrawalRequest(
                API_KEY,
                API_SECRET,
                "977903",
                XBt,
                BigDecimal.valueOf(1000L),
                "mkHS9ne12qx9pS9VojpwU5xtRd4T7X7ZUt",
                null,
                null
        );

        String requestBodyWithNeitherFeeNorText =
                "address=mkHS9ne12qx9pS9VojpwU5xtRd4T7X7ZUt&amount=1000&currency=XBt&otpToken=977903";

        WithdrawalRequest requestWithFeeAndText = new WithdrawalRequest(
                API_KEY,
                API_SECRET,
                "977903",
                XBt,
                BigDecimal.valueOf(1000L),
                "mkHS9ne12qx9pS9VojpwU5xtRd4T7X7ZUt",
                BigDecimal.valueOf(1L),
                "Transfering to my wallet"
        );

        String requestBodyWithFeeAndText =
                "address=mkHS9ne12qx9pS9VojpwU5xtRd4T7X7ZUt&amount=1000&currency=XBt&otpToken=977903&fee=1"
                        + "&text=Transfering to my wallet";

        return Stream.of(
                Arguments.of(requestWithNeitherFeeNorText, requestBodyWithNeitherFeeNorText),
                Arguments.of(requestWithFeeAndText, requestBodyWithFeeAndText)
        );
    }
}