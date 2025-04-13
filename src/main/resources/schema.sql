-- Table for user accounts
CREATE TABLE account (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         balance DOUBLE NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for cryptocurrency holdings
CREATE TABLE holding (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         account_id BIGINT NOT NULL,
                         crypto_symbol VARCHAR(10) NOT NULL,
                         quantity DOUBLE NOT NULL,
                         FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE
);

-- Table for transaction history
CREATE TABLE transaction (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             account_id BIGINT NOT NULL,
                             crypto_symbol VARCHAR(10) NOT NULL,
                             type ENUM('BUY', 'SELL') NOT NULL,
                             quantity DOUBLE NOT NULL,
                             price_per_unit DOUBLE NOT NULL,
                             total_price DOUBLE NOT NULL,
                             transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE
);