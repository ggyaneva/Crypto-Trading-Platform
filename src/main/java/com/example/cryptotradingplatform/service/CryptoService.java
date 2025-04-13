package com.example.cryptotradingplatform.service;

import jakarta.websocket.*;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@ClientEndpoint
public class CryptoService {

    private static final String KRAKEN_WEBSOCKET_URL = "wss://ws.kraken.com/v2";
    private Session session;
    private final ConcurrentMap<String, Double> cryptoPrices = new ConcurrentHashMap<>();

    //Constructor to establish the WebSocket connection to Kraken API.
    public CryptoService() {
        connectToWebSocket();
    }

    //Establishes the WebSocket connection to Kraken API.
    private void connectToWebSocket() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI(KRAKEN_WEBSOCKET_URL));
            System.out.println("Connected to Kraken WebSocket API.");
        } catch (Exception e) {
            System.err.println("Error connecting to Kraken WebSocket API: " + e.getMessage());
        }
    }

    //Handles the WebSocket connection opening.
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        subscribeToTicker();
    }

    //Handles incoming WebSocket messages containing price updates.
    @OnMessage
    public void onMessage(String message) {
        // Example message: [42, {"c":["28000.0", "1.0"]}, "ticker", "XBT/USD"]
        if (message.contains("\"ticker\"")) {
            try {
                String[] parts = message.split(",");
                String pair = parts[3].replace("\"", "").replace("]", ""); // Extract cryptocurrency pair
                String priceString = parts[1].split(":")[2].replace("[", "").replace("]", "").replace("\"", ""); // Extract price
                double price = Double.parseDouble(priceString);

                cryptoPrices.put(pair, price);
                System.out.println("Updated price for " + pair + ": " + price);
            } catch (Exception e) {
                System.err.println("Error processing message: " + message);
            }
        }
    }

    //Handles the WebSocket connection closure.
    @OnClose
    public void onClose() {
        System.out.println("WebSocket connection closed.");
    }

    //Handles any WebSocket errors that occur.
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }

    //Subscribes to the Kraken WebSocket API for ticker updates for the top cryptocurrencies.
    private void subscribeToTicker() {
        if (session != null && session.isOpen()) {
            String subscribeMessage = "{\"event\":\"subscribe\",\"pair\":["
                    + "\"XBT/USD\",\"ETH/USD\",\"XRP/USD\",\"ADA/USD\",\"DOT/USD\","
                    + "\"SOL/USD\",\"SHIB/USD\",\"LTC/USD\",\"LINK/USD\",\"BCH/USD\","
                    + "\"XLM/USD\",\"ATOM/USD\",\"FIL/USD\",\"APE/USD\",\"ICP/USD\","
                    + "\"NEAR/USD\",\"DOGE/USD\",\"MATIC/USD\""
                    + "],\"subscription\":{\"name\":\"ticker\"}}";

            session.getAsyncRemote().sendText(subscribeMessage);
            System.out.println("Subscribed to ticker updates for supported cryptocurrencies.");
        }
    }

    //Retrieves live prices for all subscribed cryptocurrencies.
    public String getLivePrices() {
        StringBuilder pricesJson = new StringBuilder("{");

        for (String pair : cryptoPrices.keySet()) {
            pricesJson.append("\"").append(pair).append("\": ").append(cryptoPrices.get(pair)).append(",");
        }

        // Remove trailing comma, if any
        if (pricesJson.length() > 1) {
            pricesJson.deleteCharAt(pricesJson.length() - 1);
        }

        pricesJson.append("}");
        return pricesJson.toString();
    }
}