package com.michelkapoko.copper.bitmex.integration.api;

import java.util.List;

public interface BitmexIntegrationService {
    List<Wallet> getWallets(WalletRequest request);
    List<Transaction> getWalletHistory(WalletHistoryRequest request);
    Transaction requestWithdrawal(WithdrawalRequest request);
}
