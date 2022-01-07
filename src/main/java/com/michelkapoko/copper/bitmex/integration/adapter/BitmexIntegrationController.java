package com.michelkapoko.copper.bitmex.integration.adapter;

import com.michelkapoko.copper.bitmex.integration.api.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Adapter receiving and parsing requests incoming into our application.
 * It is the main point of entrance of the application.
 */
@RestController
@RequestMapping("/bitmex")
public class BitmexIntegrationController {

    private final BitmexIntegrationService bitmexIntegrationService;

    public BitmexIntegrationController(BitmexIntegrationService bitmexIntegrationService) {
        this.bitmexIntegrationService = bitmexIntegrationService;
    }

    @GetMapping(value = "/wallets", produces = APPLICATION_JSON_VALUE)
    public List<Wallet> getWallets(@Validated WalletRequest request) {
        return bitmexIntegrationService.getWallets(request);
    }

    @GetMapping(value = "/walletHistory", produces = APPLICATION_JSON_VALUE)
    public List<Transaction> getWalletHistory(@Validated WalletHistoryRequest request) {
        return bitmexIntegrationService.getWalletHistory(request);
    }

    @PostMapping(value = "/requestWithdrawal", consumes = APPLICATION_JSON_VALUE)
    public Transaction requestWithdrawal(@Validated @RequestBody WithdrawalRequest request) {
        return bitmexIntegrationService.requestWithdrawal(request);
    }
}
