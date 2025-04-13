package com.example.cryptotradingplatform.controller;

import com.example.cryptotradingplatform.service.CryptoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CryptoController {

    private final CryptoService cryptoService;

    public CryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @GetMapping("/api/cryptos")
    public String getCryptoPrices() {
        return cryptoService.getLivePrices();
    }
}
