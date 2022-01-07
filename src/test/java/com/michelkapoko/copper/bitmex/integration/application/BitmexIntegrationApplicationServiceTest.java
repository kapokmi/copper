package com.michelkapoko.copper.bitmex.integration.application;

import com.michelkapoko.copper.bitmex.integration.adapter.BitmexClient;
import com.michelkapoko.copper.bitmex.integration.api.*;
import com.michelkapoko.copper.bitmex.integration.domain.WalletEntity;
import com.michelkapoko.copper.bitmex.integration.domain.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.michelkapoko.copper.bitmex.integration.api.Currency.USDt;
import static com.michelkapoko.copper.bitmex.integration.api.Currency.XBt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BitmexIntegrationApplicationServiceTest {

    private static final String API_KEY = "qVYl9mK6Usy5BMsnX971R3er";
    private static final String API_SECRET = "chNOOS4KvNXR_Xq4k4c9qsfoKWvnDecLATCRlcBwyKDYnWgO";
    private static final Instant NOW = Instant.now();

    @Mock
    private WalletRepository repository;
    @Mock
    private BitmexClient bitmexClient;
    private Clock clock;
    private BitmexIntegrationApplicationService underTest;
    @Captor
    private ArgumentCaptor<WalletEntity> walletEntityCaptor;

    @BeforeEach
    void setup(){
        clock = Clock.fixed(NOW, ZoneId.of("UTC"));
        underTest = new BitmexIntegrationApplicationService(bitmexClient, repository, clock);
    }

    @Test
    void shouldCallBitmexClientWithAppropriateParametersWhenGettingWalletHistory(){
        WalletHistoryRequest request = new WalletHistoryRequest(API_KEY, API_SECRET, USDt, 100, 0);
        underTest.getWalletHistory(request);
        verify(bitmexClient).getWalletHistory(eq(request));
    }

    @Test
    void shouldCallBitmexClientWithAppropriateParametersWhenRequestingWithdrawal(){
        WithdrawalRequest request =
                new WithdrawalRequest(API_KEY, API_SECRET, "otpToken", USDt, BigDecimal.TEN,"address" ,null, null);
        underTest.requestWithdrawal(request);
        verify(bitmexClient).requestWithdrawal(eq(request));
    }

    @Test
    void shouldCallBitmexClientAndWalletRepositoryWithAppropriateParametersWhenGettingWallets(){
        WalletRequest request = new WalletRequest(API_KEY, API_SECRET);
        Wallet expectedUSDtWallet = new Wallet(1L, USDt, BigDecimal.valueOf(100_000_000_000L), BigDecimal.ZERO);
        Wallet expectedXBtWallet = new Wallet(1L, XBt, BigDecimal.valueOf(1_053_000L), BigDecimal.valueOf(63000L));
        WalletEntity existingXBtWalletEntity = new WalletEntity(1l, XBt, BigDecimal.valueOf(1_000_000L),
                BigDecimal.ZERO, NOW.minusSeconds(1), NOW.minusSeconds(1));
        WalletEntity expectedUSDtWalletEntity = new WalletEntity(1l, USDt, BigDecimal.valueOf(100_000_000_000L), BigDecimal.ZERO, NOW, NOW);
        WalletEntity updatedXBtWalletEntity = existingXBtWalletEntity;
        updatedXBtWalletEntity.setBalance(BigDecimal.valueOf(1_053_000L));
        updatedXBtWalletEntity.setReservedFunds(BigDecimal.valueOf(63_000L));
        updatedXBtWalletEntity.setLastUpdated(NOW);

        when(bitmexClient.getWallets(eq(request))).thenReturn(bitmexWallets());
        when(repository.findByAccountIdAndCurrency(1L, USDt)).thenReturn(Optional.empty());
        when(repository.findByAccountIdAndCurrency(1L, XBt)).thenReturn(Optional.of(existingXBtWalletEntity));

        List<Wallet> wallets = underTest.getWallets(request);

        verify(bitmexClient).getWallets(eq(request));
        verify(repository,times(2)).save(walletEntityCaptor.capture());
        assertThat(walletEntityCaptor.getAllValues(), containsInAnyOrder(updatedXBtWalletEntity, expectedUSDtWalletEntity));
        assertEquals(wallets, Arrays.asList(expectedUSDtWallet, expectedXBtWallet));
    }

    private List<BitmexWallet> bitmexWallets() {
        LocalDateTime now = LocalDateTime.now(clock);
        BitmexWallet usdtWallet = new BitmexWallet(
                1l,
                USDt,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                now,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.valueOf(100000000000L),
                BigDecimal.ZERO,
                BigDecimal.valueOf(100000000000L),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                now);

        BitmexWallet xbtWallet = new BitmexWallet(
                1L,
                XBt,
                BigDecimal.valueOf(200000L),
                BigDecimal.valueOf(147000L),
                BigDecimal.valueOf(1000000L),
                BigDecimal.ZERO,
                BigDecimal.valueOf(1053000L),
                now,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.valueOf(200000),
                BigDecimal.valueOf(147000),
                BigDecimal.valueOf(1000000),
                BigDecimal.ZERO,
                BigDecimal.valueOf(1053000),
                BigDecimal.ZERO,
                BigDecimal.valueOf(63000L),
                BigDecimal.valueOf(63000L),
                now);

        return Arrays.asList(usdtWallet,xbtWallet);
    }
}