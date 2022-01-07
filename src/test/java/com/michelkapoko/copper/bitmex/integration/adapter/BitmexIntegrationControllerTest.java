package com.michelkapoko.copper.bitmex.integration.adapter;

import com.michelkapoko.copper.bitmex.integration.api.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.michelkapoko.copper.bitmex.integration.api.Currency.USDt;
import static com.michelkapoko.copper.bitmex.integration.api.Currency.XBt;
import static com.michelkapoko.copper.bitmex.integration.api.TransactStatus.PROCESSING;
import static com.michelkapoko.copper.bitmex.integration.api.TransactType.WITHDRAWAL;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class BitmexIntegrationControllerTest {

    private static final Clock  CLOCK = Clock.fixed(Instant.ofEpochSecond(1642392009), ZoneId.of("UTC"));

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BitmexIntegrationService service;
    private Clock clock;

    @Captor
    private ArgumentCaptor<WalletHistoryRequest> requestCaptor;

    @ParameterizedTest
    @MethodSource("urlsWithWrongApiKeyAndOrApiSecrets")
    void shouldThrow400WhenApiKeyOrApiSecretIsNull(String path){
        try {
                mvc.perform(get(path)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldReturnWalletsWhenGetWalletRequestIsCorrect(){
        Wallet uSDtWallet = new Wallet(396280L, USDt, BigDecimal.valueOf(100_000_000_000L), BigDecimal.ZERO);
        Wallet xBtWallet = new Wallet(396280L, XBt, BigDecimal.valueOf(1_053_000L), BigDecimal.valueOf(63000L));
        when(service.getWallets(any(WalletRequest.class))).thenReturn(Arrays.asList(uSDtWallet, xBtWallet));

        try {
            mvc.perform(get("/bitmex/wallets?apiKey=key&apiSecret=secret")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string(wallets()));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @ParameterizedTest
    @ValueSource(strings={"-1", "501"})
    void walletHistoryRequestShouldReturn400IfCountIsOutOfBoundaries(String count) {
        String url = "/bitmex/walletHistory?apiKey=key&apiSecret=secret&currency=all&startingPoint=1&count=" + count;
        try {
            mvc.perform(get(url)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @ParameterizedTest
    @ValueSource(strings={"0","1","499", "500"})
    void shouldReturnWalletHistoryIfAllParametersAreValid(String count) {
        String url = "/bitmex/walletHistory?apiKey=key&apiSecret=secret&currency=all&startingPoint=0&count=" + count;
        when(service.getWalletHistory(any(WalletHistoryRequest.class))).thenReturn(transactions());
        try {
            mvc.perform(get(url)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string(walletHistory()));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void walletHistoryRequestShouldReturn400IfStartingPointIsOutOfBoundaries() {
        String url = "/bitmex/walletHistory?apiKey=key&apiSecret=secret&currency=all&startingPoint=-1&count=100";
        try {
            mvc.perform(get(url)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void properlySetTheDefaultValuesForWalletHistoryRequest() {
        String url = "/bitmex/walletHistory?apiKey=key&apiSecret=secret";
        try {
            mvc.perform(get(url)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(service).getWalletHistory(requestCaptor.capture());
            assertEquals(new WalletHistoryRequest("key", "secret", XBt, 100, 0),
                    requestCaptor.getValue());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @ParameterizedTest
    @MethodSource("faultyWithdrawalRequests")
    void withdrawalRequestShouldReturn400IfOneOfTheCompulsoryPropertiesIsNull(String request) {
        String url = "/bitmex/requestWithdrawal";
        try {
            mvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void withdrawalRequestShouldReturnAWithdrawwalTransactionIfAllParametersAreCorrect() {
        String url = "/bitmex/requestWithdrawal";
        when(service.requestWithdrawal(any(WithdrawalRequest.class))).thenReturn(xBtTransaction());
        try {
            mvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(withdrawalRequest()))
                    .andExpect(status().isOk())
                    .andExpect(content().string(transactionAsJson()));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static Stream<Arguments> urlsWithWrongApiKeyAndOrApiSecrets() {
        return Stream.of(
                Arguments.of("/bitmex/wallets?apiSecret=secret"),
                Arguments.of("/bitmex/wallets?apiKey=key"),
                Arguments.of("/bitmex/wallets"),
                Arguments.of("/bitmex/walletHistory?apiSecret=secret&currency=all&count=100&startingPoint=1"),
                Arguments.of("/bitmex/walletHistory?apiKey=key&currency=all&count=100&startingPoint=1"),
                Arguments.of("/bitmex/walletHistory?currency=all&count=100&startingPoint=1")
        );
    }

    private String wallets() {
        return "[{"
                + "\"accountId\":396280,"
                + "\"currency\":\"USDt\","
                + "\"balance\":100000000000,"
                + "\"reservedFunds\":0"
                + "},"
                + "{"
                + "\"accountId\":396280,"
                + "\"currency\":\"XBt\","
                + "\"balance\":1053000,"
                + "\"reservedFunds\":63000"
                + "}]";
    }

    private List<Transaction> transactions() {
        Transaction xBtTransaction = xBtTransaction();
        return singletonList(xBtTransaction);
    }

    private Transaction xBtTransaction() {
        LocalDateTime time = LocalDateTime.ofInstant(CLOCK.instant(), ZoneId.of("UTC")) ;
        return new Transaction(
                "f822d981-35be-ffb6-2af8-d6c75d3d00be",
                396280L,
                XBt,
                WITHDRAWAL,
                BigDecimal.valueOf(-21000),
                BigDecimal.valueOf(20000),
                PROCESSING,
                "mkHS9ne12qx9pS9VojpwU5xtRd4T7X7ZUt",
                "",
                "Transfering back to faucet",
                time,
                time,
                BigDecimal.valueOf(1_011_000L),
                null);
    }

    private String walletHistory() {
        return "[" + transactionAsJson() + "]";
    }

    private String transactionAsJson() {
        return "{"
                +       "\"transactID\":\"f822d981-35be-ffb6-2af8-d6c75d3d00be\","
                +       "\"account\":396280,"
                +       "\"currency\":\"XBt\","
                +       "\"transactType\":\"Withdrawal\","
                +       "\"amount\":-21000,"
                +       "\"fee\":20000,"
                +       "\"transactStatus\":\"Processing\","
                +       "\"address\":\"mkHS9ne12qx9pS9VojpwU5xtRd4T7X7ZUt\","
                +       "\"tx\":\"\","
                +       "\"text\":\"Transfering back to faucet\","
                +       "\"transactTime\":\"" + LocalDateTime.ofInstant(CLOCK.instant(), ZoneId.of("UTC")) + "\","
                +       "\"timestamp\":\"" + LocalDateTime.ofInstant(CLOCK.instant(), ZoneId.of("UTC")) + "\","
                +       "\"walletBalance\":1011000,"
                +       "\"marginBalance\":null"
                +   "}";
    }

    private static Stream<Arguments> faultyWithdrawalRequests() {
        String[] requests = new String[]{
                "{"
                        + "\"apiSecret\":\"secret\","
                        + "\"currency\":\"XBt\","
                        + "\"amount\":100000000000,"
                        + "\"address\":\"mkHS9ne12qx9pS9VojpwU5xtRd4T7X7ZUt\","
                        + "\"otpToken\":250039"
                        + "}",
                "{"
                        + "\"apiKey\":\"key\","
                        + "\"currency\":\"XBt\","
                        + "\"amount\":100000000000,"
                        + "\"address\":\"mkHS9ne12qx9pS9VojpwU5xtRd4T7X7ZUt\","
                        + "\"otpToken\":250039"
                        + "}",
                "{"
                        + "\"apiKey\":\"key\","
                        + "\"apiSecret\":\"secret\","
                        + "\"amount\":100000000000,"
                        + "\"address\":\"mkHS9ne12qx9pS9VojpwU5xtRd4T7X7ZUt\","
                        + "\"otpToken\":250039"
                        + "}",
                "{"
                        + "\"apiKey\":\"key\","
                        + "\"apiSecret\":\"secret\","
                        + "\"currency\":\"XBt\","
                        + "\"address\":\"mkHS9ne12qx9pS9VojpwU5xtRd4T7X7ZUt\","
                        + "\"otpToken\":250039"
                        + "}",
                "{"
                        + "\"apiKey\":\"key\","
                        + "\"apiSecret\":\"secret\","
                        + "\"currency\":\"XBt\","
                        + "\"amount\":100000000000,"
                        + "\"otpToken\":250039"
                        + "}",
                "{"
                        + "\"apiKey\":\"key\","
                        + "\"apiSecret\":\"secret\","
                        + "\"currency\":\"XBt\","
                        + "\"amount\":100000000000,"
                        + "\"address\":\"mkHS9ne12qx9pS9VojpwU5xtRd4T7X7ZUt\""
                        + "}",
                "{"
                        + "\"apiKey\":\"key\","
                        + "\"apiSecret\":\"secret\","
                        + "\"currency\":\"all\","
                        + "\"amount\":100000000000,"
                        + "\"address\":\"mkHS9ne12qx9pS9VojpwU5xtRd4T7X7ZUt\","
                        + "\"otpToken\":250039"
                        + "}"
        };

        return Stream.of(requests).map(Arguments::of);
    }

    private String withdrawalRequest() {
        return "{"
                + "\"apiKey\":\"key\","
                + "\"apiSecret\":\"secret\","
                + "\"currency\":\"XBt\","
                + "\"amount\":1000,"
                + "\"address\":\"mkHS9ne12qx9pS9VojpwU5xtRd4T7X7ZUt\","
                + "\"otpToken\":250039"
                + "}";
    }
}