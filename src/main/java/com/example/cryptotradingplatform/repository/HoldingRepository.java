package com.example.cryptotradingplatform.repository;

import com.example.cryptotradingplatform.model.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, Long> {
    Optional<Holding> findByAccountIdAndCryptoSymbol(Long accountId, String cryptoSymbol);
}

