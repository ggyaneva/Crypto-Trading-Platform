package com.example.cryptotradingplatform.controller;

import com.example.cryptotradingplatform.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    //Buy cryptocurrency
    @PostMapping("/buy")
    public ResponseEntity<String> buyCrypto(@RequestParam Long accountId,
                                            @RequestParam String cryptoSymbol,
                                            @RequestParam double quantity,
                                            @RequestParam double pricePerUnit) {
        String result = accountService.buyCrypto(accountId, cryptoSymbol, quantity, pricePerUnit);
        if (result.contains("successful")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    //Sell cryptocurrency
    @PostMapping("/sell")
    public ResponseEntity<String> sellCrypto(@RequestParam Long accountId,
                                             @RequestParam String cryptoSymbol,
                                             @RequestParam double quantity,
                                             @RequestParam double pricePerUnit) {
        String result = accountService.sellCrypto(accountId, cryptoSymbol, quantity, pricePerUnit);
        if (result.contains("successful")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}