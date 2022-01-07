package com.michelkapoko.copper.bitmex.integration.domain;

import com.michelkapoko.copper.bitmex.integration.api.Currency;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends CrudRepository<WalletEntity, Long> {
    Optional<WalletEntity> findByAccountIdAndCurrency(Long accountId, Currency currency);
}
