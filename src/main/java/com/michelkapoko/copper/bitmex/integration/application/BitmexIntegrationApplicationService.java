package com.michelkapoko.copper.bitmex.integration.application;

import com.michelkapoko.copper.bitmex.integration.adapter.BitmexClient;
import com.michelkapoko.copper.bitmex.integration.api.*;
import com.michelkapoko.copper.bitmex.integration.domain.WalletEntity;
import com.michelkapoko.copper.bitmex.integration.domain.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BitmexIntegrationApplicationService implements BitmexIntegrationService {

    private final BitmexClient bitmexClient;
    private final WalletRepository repository;
    private final Clock clock;

    public BitmexIntegrationApplicationService(BitmexClient bitmexClient, WalletRepository repository, Clock clock) {
        this.bitmexClient = bitmexClient;
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public List<Wallet> getWallets(WalletRequest request) {
        List<Wallet> wallets = mapWallets(bitmexClient.getWallets(request));
        upsertWallets(wallets);
        return wallets;
    }

    @Override
    public List<Transaction> getWalletHistory(WalletHistoryRequest request) {
        return bitmexClient.getWalletHistory(request);
    }

    @Override
    public Transaction requestWithdrawal(WithdrawalRequest request) {
        return bitmexClient.requestWithdrawal(request);
    }

    private List<Wallet> mapWallets(List<BitmexWallet> bitmexWallets) {
        return bitmexWallets.stream()
                .map(this::mapWallet)
                .collect(Collectors.toList());
    }

    private Wallet mapWallet(BitmexWallet bitmexWallet) {
        BigDecimal balance = bitmexWallet.getAmount().add(bitmexWallet.getPendingCredit());
        BigDecimal reservedFund = bitmexWallet.getPendingCredit().add(bitmexWallet.getPendingDebit());
        return new Wallet(bitmexWallet.getAccount(), bitmexWallet.getCurrency(), balance, reservedFund);
    }

    private List<WalletEntity> upsertWallets(List<Wallet> wallets) {
        List<WalletEntity> updatedEntities = wallets.stream()
                .map(wallet -> updateWallet(wallet))
                .collect(Collectors.toList());
        return updatedEntities;
    }

    private WalletEntity updateWallet(Wallet wallet) {
        Optional<WalletEntity> currentEntity =
                repository.findByAccountIdAndCurrency(wallet.getAccountId(), wallet.getCurrency());
        WalletEntity updatedEntity = null;
         if (currentEntity.isEmpty()) {
             updatedEntity = mapToEntity(wallet);
         }  else {
             updatedEntity = currentEntity.get();
             updateEntity(updatedEntity, wallet);
         }
        return repository.save(updatedEntity);
    }

    private WalletEntity mapToEntity(Wallet wallet) {
        Instant now = clock.instant();
        return new WalletEntity(
                wallet.getAccountId(),
                wallet.getCurrency(),
                wallet.getBalance(),
                wallet.getReservedFunds(),
                now,
                now);
    }

    private void updateEntity(WalletEntity walletEntity, Wallet wallet) {
        walletEntity.setBalance(wallet.getBalance());
        walletEntity.setReservedFunds(wallet.getReservedFunds());
        walletEntity.setLastUpdated(clock.instant());
    }
}
