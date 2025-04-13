class CryptoTradingPlatform {
    constructor() {
        // Initialize state
        this.balance = 10000.0;
        this.ownedCryptos = {};
        this.averageBuyPrice = {};
        this.currentTransaction = { type: null, symbol: null, price: 0 };

        // DOM Elements
        this.pricesTable = document.getElementById('prices-table');
        this.balanceElement = document.getElementById('balance');
        this.resetButton = document.getElementById('reset-balance');
        this.transactionHistory = document.getElementById('transaction-history');
        this.modal = document.getElementById('transaction-modal');
        this.modalTitle = document.getElementById('modal-title');
        this.modalMessage = document.getElementById('modal-message');
        this.quantityInput = document.getElementById('quantity');
        this.confirmButton = document.getElementById('confirm-transaction');
        this.closeButton = document.querySelector('.close-button');

        // Error Modal
        this.errorModal = document.createElement('div');
        this.errorModal.className = 'modal';
        document.body.appendChild(this.errorModal);
        this.errorModal.innerHTML = `
            <div class="modal-content">
                <span class="close-button error-close">&times;</span>
                <h3 id="error-modal-title">Message</h3>
                <p id="error-modal-message"></p>
            </div>
        `;
        this.errorTitle = document.getElementById('error-modal-title');
        this.errorMessage = document.getElementById('error-modal-message');
        this.errorCloseButton = document.querySelector('.error-close');

        // Initialize WebSocket and Event Listeners
        this._bindEventListeners();
        this._populatePricesTable();
        this._initializeWebSocket();
    }

    _bindEventListeners() {
        // Event listeners for buttons and modal
        this.resetButton.addEventListener('click', this._resetAccount.bind(this));
        this.confirmButton.addEventListener('click', this._confirmTransaction.bind(this));
        this.closeButton.addEventListener('click', () => (this.modal.style.display = 'none'));
        this.errorCloseButton.addEventListener('click', () => (this.errorModal.style.display = 'none'));
        window.addEventListener('click', (event) => {
            if (event.target === this.errorModal) this.errorModal.style.display = 'none';
        });
    }

    _populatePricesTable() {
        const cryptocurrencies = [
            { name: "Bitcoin", symbol: "XBT/USD" },
            { name: "Ethereum", symbol: "ETH/USD" },
            { name: "Tether", symbol: "USDT/USD" },
            { name: "Polkadot", symbol: "DOT/USD" },
            { name: "USDC", symbol: "USDC/USD" },
            { name: "Solana", symbol: "SOL/USD" },
            { name: "Dogecoin", symbol: "XDG/USD" },
            { name: "TRON", symbol: "TRX/USD" },
            { name: "Cardano", symbol: "ADA/USD" },
            { name: "Wrapped Bitcoin", symbol: "WBTC/USD" },
            { name: "Chainlink", symbol: "LINK/USD" },
            { name: "USDS", symbol: "USDS/USD" },
            { name: "Avalanche", symbol: "AVAX/USD" },
            { name: "Toncoin", symbol: "TON/USD" },
            { name: "Monero", symbol: "XMR/USD" },
            { name: "Aptos", symbol: "APT/USD" },
            { name: "Sui", symbol: "SUI/USD" },
            { name: "Mantra", symbol: "OM/USD" },
            { name: "Bitcoin Cash", symbol: "BCH/USD" },
            { name: "Litecoin", symbol: "LTC/USD" }
        ];

        cryptocurrencies.forEach((crypto, index) => {
            const row = document.createElement('tr');
            row.setAttribute('data-pair', crypto.symbol);
            row.innerHTML = `
                <td>${index + 1}</td>
                <td>${crypto.name}</td>
                <td>${crypto.symbol.split('/')[0]}</td>
                <td class="price">Loading...</td>
                <td>
                    <button class="buy-button" data-symbol="${crypto.symbol}">Buy</button>
                    <button class="sell-button" data-symbol="${crypto.symbol}">Sell</button>
                </td>
            `;
            this.pricesTable.appendChild(row);

            row.querySelector('.buy-button').addEventListener('click', () =>
                this._handleTransaction('buy', crypto.symbol)
            );
            row.querySelector('.sell-button').addEventListener('click', () =>
                this._handleTransaction('sell', crypto.symbol)
            );
        });
    }

    _initializeWebSocket() {
        this.socket = new WebSocket('wss://ws.kraken.com/');
        this.socket.onopen = this._subscribeToWebSocket.bind(this);
        this.socket.onmessage = this._handleWebSocketMessage.bind(this);
        this.socket.onerror = this._handleWebSocketError.bind(this);
        this.socket.onclose = this._handleWebSocketClose.bind(this);
    }

    _subscribeToWebSocket() {
        const subscribeMessage = {
            event: 'subscribe',
            pair: ["XBT/USD", "ETH/USD", "USDT/USD", "DOT/USD", "USDC/USD",
                "SOL/USD", "XDG/USD", "TRX/USD", "ADA/USD", "WBTC/USD",
                "LINK/USD", "USDS/USD", "AVAX/USD", "TON/USD", "XMR/USD",
                "APT/USD", "SUI/USD", "OM/USD", "BCH/USD", "LTC/USD"],
            subscription: { name: 'ticker' },
        };
        this.socket.send(JSON.stringify(subscribeMessage));
    }

    _handleWebSocketMessage(event) {
        const data = JSON.parse(event.data);
        if (Array.isArray(data) && data[1]) {
            const priceInfo = data[1];
            const pair = data[3]; // e.g., "XBT/USD"
            const price = parseFloat(priceInfo.c[0]); // Current price

            // Update the prices table
            const row = document.querySelector(`tr[data-pair="${pair}"]`);
            if (row) {
                row.querySelector('.price').textContent = `$${price.toFixed(2)}`;
            }
        }
    }

    _handleWebSocketError() {
        console.error('WebSocket encountered an error.');
        this._showErrorModal('WebSocket Error', 'An error occurred with the WebSocket connection.');
    }

    _handleWebSocketClose() {
        console.warn('WebSocket connection closed. Reconnecting in 5 seconds...');
        setTimeout(() => this._initializeWebSocket(), 5000); // Retry connection
    }

    _resetAccount() {
        this.balance = 10000.0;
        this.balanceElement.textContent = this.balance.toFixed(2);
        this.transactionHistory.innerHTML = '';
        this.ownedCryptos = {};
        this.averageBuyPrice = {};
        this._showErrorModal(
            'Account Reset',
            'Your account has been reset to the initial balance of $10,000.00.'
        );
    }

    _showErrorModal(title, message) {
        this.errorTitle.textContent = title;
        this.errorMessage.textContent = message;
        this.errorModal.style.display = 'block';
    }

    _handleTransaction(type, symbol) {
        const row = document.querySelector(`tr[data-pair="${symbol}"]`);
        const priceText = row.querySelector('.price').textContent; // Get live price from DOM
        const price = parseFloat(priceText.replace('$', '')); // Convert price to a number

        if (isNaN(price)) {
            this._showErrorModal('Error', 'Unable to fetch the live price. Please try again.');
            return;
        }

        this.currentTransaction = { type, symbol, price };

        if (type === 'sell') {
            if (!this.ownedCryptos[symbol] || this.ownedCryptos[symbol] <= 0) {
                this._showErrorModal('Sell Error', `You do not own any ${symbol} to sell.`);
                return;
            }
            this.modalMessage.textContent = `You own ${this.ownedCryptos[symbol].toFixed(
                2
            )} ${symbol}. Price: $${price.toFixed(2)}`;
        } else {
            this.modalMessage.textContent = `Live Price: $${price.toFixed(2)}`;
        }

        this.modalTitle.textContent = `${type === 'buy' ? 'Buy' : 'Sell'} ${symbol}`;
        this.quantityInput.value = '';
        this.modal.style.display = 'block';
    }

    _confirmTransaction() {
        const quantity = parseFloat(this.quantityInput.value);

        if (isNaN(quantity) || quantity <= 0) {
            this._showErrorModal('Invalid Quantity', 'Please enter a valid quantity.');
            return;
        }

        const total = quantity * this.currentTransaction.price;

        if (this.currentTransaction.type === 'buy') {
            if (total > this.balance) {
                this._showErrorModal('Buy Error', 'Insufficient balance to complete the transaction.');
                return;
            }
            this.balance -= total;

            if (!this.ownedCryptos[this.currentTransaction.symbol]) {
                this.ownedCryptos[this.currentTransaction.symbol] = 0;
                this.averageBuyPrice[this.currentTransaction.symbol] = 0;
            }

            const currentOwned = this.ownedCryptos[this.currentTransaction.symbol];
            const newTotalCost =
                this.averageBuyPrice[this.currentTransaction.symbol] * currentOwned + total;
            this.ownedCryptos[this.currentTransaction.symbol] += quantity;
            this.averageBuyPrice[this.currentTransaction.symbol] =
                newTotalCost / this.ownedCryptos[this.currentTransaction.symbol];

            this._addTransactionToHistory('Buy', this.currentTransaction.symbol, quantity, this.currentTransaction.price);
        } else {
            if (
                !this.ownedCryptos[this.currentTransaction.symbol] ||
                this.ownedCryptos[this.currentTransaction.symbol] < quantity
            ) {
                this._showErrorModal(
                    'Sell Error',
                    `You do not own enough ${this.currentTransaction.symbol} to sell.`
                );
                return;
            }

            this.balance += total;
            this.ownedCryptos[this.currentTransaction.symbol] -= quantity;

            const profitOrLoss =
                (this.currentTransaction.price - this.averageBuyPrice[this.currentTransaction.symbol]) *
                quantity;
            const profitOrLossText =
                profitOrLoss >= 0
                    ? `Profit: $${profitOrLoss.toFixed(2)}`
                    : `Loss: $${Math.abs(profitOrLoss).toFixed(2)}`;

            this._addTransactionToHistory(
                'Sell',
                this.currentTransaction.symbol,
                quantity,
                this.currentTransaction.price,
                profitOrLossText
            );
        }

        this.balanceElement.textContent = this.balance.toFixed(2);
        this.modal.style.display = 'none';
    }

    _addTransactionToHistory(type, symbol, quantity, price, profitOrLossText = '') {
        const listItem = document.createElement('li');
        listItem.textContent = `${type} ${quantity} of ${symbol} at $${price.toFixed(
            2
        )} each ${profitOrLossText}`;
        this.transactionHistory.appendChild(listItem);
    }
}

// Initialize the platform
document.addEventListener('DOMContentLoaded', () => {
    new CryptoTradingPlatform();
});