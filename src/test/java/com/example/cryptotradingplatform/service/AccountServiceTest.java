package com.example.cryptotradingplatform.service;

import com.example.cryptotradingplatform.exception.InsufficientBalanceException;
import com.example.cryptotradingplatform.exception.InsufficientHoldingException;
import com.example.cryptotradingplatform.model.Account;
import com.example.cryptotradingplatform.model.Holding;
import com.example.cryptotradingplatform.model.Transaction;
import com.example.cryptotradingplatform.repository.AccountRepository;
import com.example.cryptotradingplatform.repository.HoldingRepository;
import com.example.cryptotradingplatform.repository.TransactionRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private HoldingRepository holdingRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountService accountService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testBuyCrypto_Success() {
        Long accountId = 1L;
        String cryptoSymbol = "BTC";
        double quantity = 2.0;
        double pricePerUnit = 50000.0;
        double balance = 200000.0;

        Account account = new Account();
        account.setBalance(balance);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(holdingRepository.findByAccountIdAndCryptoSymbol(accountId, cryptoSymbol)).thenReturn(Optional.empty());

        String result = accountService.buyCrypto(accountId, cryptoSymbol, quantity, pricePerUnit);

        assertEquals("Purchase successful!", result);
        verify(accountRepository).save(account);
        verify(holdingRepository).save(any(Holding.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    public void testBuyCrypto_InsufficientBalance_GracefulHandling() {
        Long accountId = 1L;
        String cryptoSymbol = "BTC";
        double quantity = 2.0;
        double pricePerUnit = 50000.0;
        double balance = 50000.0;

        Account account = new Account();
        account.setBalance(balance);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        try {
            accountService.buyCrypto(accountId, cryptoSymbol, quantity, pricePerUnit);
            fail("Expected InsufficientBalanceException to be thrown");
        } catch (InsufficientBalanceException e) {
            assertEquals("Insufficient balance to complete the purchase.", e.getMessage());
        }
    }

    @Test
    public void testSellCrypto_Success() {
        Long accountId = 1L;
        String cryptoSymbol = "BTC";
        double quantity = 1.0;
        double pricePerUnit = 50000.0;

        Account account = new Account();
        account.setBalance(0.0);

        Holding holding = new Holding(account, cryptoSymbol, 2.0);

        when(holdingRepository.findByAccountIdAndCryptoSymbol(accountId, cryptoSymbol)).thenReturn(Optional.of(holding));

        String result = accountService.sellCrypto(accountId, cryptoSymbol, quantity, pricePerUnit);

        assertEquals("Sale successful!", result);
        verify(holdingRepository).save(holding);
        verify(accountRepository).save(account);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    public void testSellCrypto_NoHoldings_GracefulHandling() {
        Long accountId = 1L;
        String cryptoSymbol = "BTC";

        when(holdingRepository.findByAccountIdAndCryptoSymbol(accountId, cryptoSymbol)).thenReturn(Optional.empty());

        try {
            accountService.sellCrypto(accountId, cryptoSymbol, 1.0, 50000.0);
            fail("Expected InsufficientHoldingException to be thrown");
        } catch (InsufficientHoldingException e) {
            assertEquals("You do not own any of this cryptocurrency.", e.getMessage());
        }
    }
}