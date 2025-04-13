package com.example.cryptotradingplatform.service;


import com.example.cryptotradingplatform.model.Account;
import com.example.cryptotradingplatform.model.Holding;
import com.example.cryptotradingplatform.model.Transaction;
import com.example.cryptotradingplatform.model.TransactionType;
import com.example.cryptotradingplatform.repository.AccountRepository;
import com.example.cryptotradingplatform.repository.HoldingRepository;
import com.example.cryptotradingplatform.repository.TransactionRepository;
import com.example.cryptotradingplatform.exception.AccountNotFoundException;
import com.example.cryptotradingplatform.exception.InsufficientBalanceException;
import com.example.cryptotradingplatform.exception.InsufficientHoldingException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final HoldingRepository holdingRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository, HoldingRepository holdingRepository,
                          TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.holdingRepository = holdingRepository;
        this.transactionRepository = transactionRepository;
    }

    public String buyCrypto(Long accountId, String cryptoSymbol, double quantity, double pricePerUnit) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found."));
        double totalCost = quantity * pricePerUnit;

        if (account.getBalance() < totalCost) {
            throw new InsufficientBalanceException("Insufficient balance to complete the purchase.");
        }

        account.setBalance(account.getBalance() - totalCost);
        accountRepository.save(account);

        Optional<Holding> existingHolding = holdingRepository.findByAccountIdAndCryptoSymbol(accountId, cryptoSymbol);
        existingHolding.ifPresentOrElse(
                holding -> {
                    holding.setQuantity(holding.getQuantity() + quantity);
                    holdingRepository.save(holding);
                },
                () -> holdingRepository.save(new Holding(account, cryptoSymbol, quantity))
        );

        Transaction transaction = new Transaction(account, cryptoSymbol, TransactionType.BUY, quantity, pricePerUnit, totalCost);
        transactionRepository.save(transaction);

        return "Purchase successful!";
    }

    public String sellCrypto(Long accountId, String cryptoSymbol, double quantity, double pricePerUnit) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        Holding holding = holdingRepository.findByAccountIdAndCryptoSymbol(accountId, cryptoSymbol)
                .orElseThrow(() -> new InsufficientHoldingException("You do not own any of this cryptocurrency."));

        if (holding.getQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient holdings to complete the sale.");
        }

        Account account = holding.getAccount();
        double totalAmount = quantity * pricePerUnit;

        holding.setQuantity(holding.getQuantity() - quantity);
        if (holding.getQuantity() == 0) {
            holdingRepository.delete(holding);
        } else {
            holdingRepository.save(holding);
        }

        account.setBalance(account.getBalance() + totalAmount);
        accountRepository.save(account);

        Transaction transaction = new Transaction(account, cryptoSymbol, TransactionType.SELL, quantity, pricePerUnit, totalAmount);
        transactionRepository.save(transaction);

        return "Sale successful!";
    }
}