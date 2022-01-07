package com.michelkapoko.copper.bitmex.integration.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static com.michelkapoko.copper.bitmex.integration.api.Currency.USDt;
import static com.michelkapoko.copper.bitmex.integration.api.Currency.XBt;
import static java.math.BigDecimal.TEN;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class WalletRepositoryTest {

    @Autowired
    private WalletRepository underTest;
    private Instant now;
    @BeforeEach
    void setup(){

        underTest.deleteAll();
        now = Instant.now();
    }

    @Test
    void shouldCorrectlyPersistNewWalletEntity(){
        WalletEntity entityToPersist = new WalletEntity(1L, XBt, TEN, TEN, now, now);
        WalletEntity entityPersisted = underTest.save(entityToPersist);
        reflectionEquals(entityToPersist, entityPersisted, "walletId");
        assertThat(entityPersisted.getWalletId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowAnExceptionWhenTryingToPersistTheSameAccountIdCurrencyCombinationTwice(){
        assertThatExceptionOfType(DataIntegrityViolationException.class).isThrownBy(
                () -> {
                        WalletEntity entityToPersist = new WalletEntity(1L, XBt, TEN, TEN, now, now);
                        underTest.save(entityToPersist);
                        // New entityId but same content and especially same currency and accountId
                        WalletEntity entityToPersist2 = new WalletEntity(1L, XBt, TEN, TEN,
                                now.plusSeconds(1), now.plusSeconds(1));
                        underTest.save(entityToPersist2);
                });
    }

    @Test
    void shouldCorrectlyFindByAccountIdAndCurrencyIfThereAreMatchingEntries(){
        WalletEntity entityToPersist1 = new WalletEntity(1L, XBt, TEN, TEN, now, now);
        underTest.save(entityToPersist1);
        WalletEntity entityToPersist2 = new WalletEntity(1L, USDt, TEN, TEN, now, now);
        underTest.save(entityToPersist2);
        WalletEntity entityToPersist3 = new WalletEntity(2L, USDt, TEN, TEN, now, now);
        underTest.save(entityToPersist3);
        Optional<WalletEntity> foundEntity1 = underTest.findByAccountIdAndCurrency(1L, USDt);
        assertThat(foundEntity1.isPresent());
        reflectionEquals(entityToPersist2, foundEntity1, "walletId");
        Optional<WalletEntity> foundEntity2 = underTest.findByAccountIdAndCurrency(2L, XBt);
        assertThat(foundEntity2.isEmpty());
    }

    @Test
    void shouldUpdateAnExistingEntityCorrectly(){
        WalletEntity entityToPersist1 = new WalletEntity(1L, XBt, TEN, TEN, now, now);
        underTest.save(entityToPersist1);
        Optional<WalletEntity> foundEntity1 = underTest.findByAccountIdAndCurrency(1L, XBt
        );
        WalletEntity updatedEntity = foundEntity1.get();
        updatedEntity.setBalance(BigDecimal.ZERO);
        underTest.save(updatedEntity);
        WalletEntity foundEntity2 = underTest.findByAccountIdAndCurrency(1L, XBt
        ).get();
        reflectionEquals(foundEntity1.get(), foundEntity2, "balance");

        assertEquals(BigDecimal.ZERO,
                BigDecimal.valueOf(foundEntity2.getBalance().longValue()));
    }
}