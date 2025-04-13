# Crypto Trading Platform

The Crypto Trading Platform is a modern web-based application that allows users to manage their cryptocurrency accounts, view live crypto prices, and perform buy/sell transactions. It features real-time updates using WebSocket communication and provides an intuitive user interface.

---

## Features
### Screenshots of the platform:
(media/pic1.png)
(media/pic2.png)

---

## Demo Video

Watch the platform in action:
(media/demo.mp4)


### Backend
1. **Account Management**:
   - Buy and sell cryptocurrencies.
   - Manage account balance and holdings.
   - Log transaction history.

2. **Live Cryptocurrency Prices**:
   - Real-time price updates for top cryptocurrencies using WebSocket.

3. **Error Handling**:
   - Custom exceptions for handling errors like insufficient balance or invalid accounts.

---

### Frontend
1. **Live Prices**:
   - Displays live cryptocurrency prices in a table format.

2. **Transaction History**:
   - Logs all buy/sell transactions with details.

3. **User Account Management**:
   - Reset account to initial balance.
   - View account balance and holdings.

4. **Interactive Modals**:
   - Buy/Sell modals for entering transaction details.

---

## Tech Stack

### Backend
- **Java 17**
- **Spring Boot 3**
- **Jakarta Persistence API (JPA)** for ORM
- **MySQL** for database storage
- **WebSocket API** for real-time price updates

### Frontend
- **HTML5, CSS3, JavaScript**
- Interactive modals for user actions
- Real-time UI updates via WebSocket

---

## Installation Guide

### Prerequisites
1. **Java 17** or later
2. **Maven** or **Gradle** for dependency management
3. **MySQL** for database
4. A WebSocket-enabled browser (e.g., Chrome, Firefox)

---

### Steps to Run the Project

#### Backend
1. Clone the repository:
   ```bash
   git clone https://github.com/ggyaneva/crypto-trading-platform.git
   cd crypto-trading-platform
   ```

2. Set up the database:
   - Create a new MySQL database.
   - Run the `schema.sql` file located in the `src/main/resources` directory to create the required tables.

3. Configure database connection:
   - Update the `src/main/resources/application.properties` file with your MySQL credentials.

4. Set your MySQL password in application.properties:

5. Build and run the backend:
   - Using Maven:
     ```bash
     mvn clean install
     mvn spring-boot:run
     ```
   - Using Gradle:
     ```bash
     gradle build
     gradle bootRun
     ```

#### Frontend
1. Serve the frontend:
   - Open the `index.html` file in any WebSocket-compatible browser.

---

## API Endpoints

### Account APIs
| Endpoint          | Method | Parameters                                                                 | Description                                                                 |
|-------------------|--------|-----------------------------------------------------------------------------|-----------------------------------------------------------------------------|
| `/api/account/buy`  | POST   | `accountId`, `cryptoSymbol`, `quantity`, `pricePerUnit`                     | Buy cryptocurrency for a specific account.                                 |
| `/api/account/sell` | POST   | `accountId`, `cryptoSymbol`, `quantity`, `pricePerUnit`                     | Sell cryptocurrency for a specific account.                                |

### Crypto APIs
| Endpoint       | Method | Description                                  |
|----------------|--------|----------------------------------------------|
| `/api/cryptos` | GET    | Returns live cryptocurrency prices in JSON. |

---

## Database Schema

### Tables
1. **`account`**:
   - Stores user account information (`id`, `balance`, `created_at`).
2. **`holding`**:
   - Tracks cryptocurrency holdings for each user account (`id`, `account_id`, `crypto_symbol`, `quantity`).
3. **`transaction`**:
   - Logs buy/sell transactions with details like quantity, price, and type (`id`, `account_id`, `crypto_symbol`, `type`, `quantity`, `price_per_unit`, `total_price`, `transaction_date`).

### SQL Schema
```sql
CREATE TABLE account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    balance DOUBLE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE holding (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    crypto_symbol VARCHAR(10) NOT NULL,
    quantity DOUBLE NOT NULL,
    FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE
);

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
```

---

## Design Decisions

### Backend
1. **Modular Architecture**:
   - Separate layers for controllers, services, and repositories ensure scalability and maintainability.
2. **WebSocket Integration**:
   - Real-time price updates using WebSocket for seamless user experience.

### Frontend
1. **Interactive UI**:
   - Real-time price updates and transaction handling with JavaScript.
2. **Dynamic Table Rendering**:
   - Renders crypto prices and actions dynamically.

---

## Future Improvements
1. **Authentication**:
   - Add user authentication.
2. **More Cryptocurrencies**:
   - Expand the list of supported cryptocurrencies.
3. **Improved Error Handling**:
   - Implement more robust exception handling and user feedback.
4. **Deployment**:
   - Deploy the application to a cloud service.

## Contributors
- Gabriela Yaneva (https://github.com/ggyaneva)

For any questions or issues, feel free to open an issue in the repository.
