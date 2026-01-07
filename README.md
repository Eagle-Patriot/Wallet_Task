# Wallet Management System

A Spring Boot REST API for managing digital wallets with bank account linking and multi-gateway payment processing.

## Features

- **Wallet Management**: Create wallets with unique email addresses
- **Bank Account Linking**: Link multiple bank accounts to wallets
- **Multi-Gateway Payments**: Support for Flutterwave and Paystack (extensible for more)
- **Atomic Transactions**: All-or-nothing payment processing with automatic rollback on failure
- **Transaction History**: Complete audit trail of all wallet activities
- **Comprehensive Testing**: 26 unit tests with 100% success rate

## Technologies

- Java 21
- Spring Boot 3.4.1
- H2 Database
- Flyway (Database migrations)
- JPA/Hibernate
- SpringDoc OpenAPI (Swagger)
- JUnit 5 & Mockito

## Quick Start

### Run the Application
```bash
./mvnw spring-boot:run
```

### Run Tests
```bash
./mvnw test
```

### Access Swagger UI
```
http://localhost:8080/swagger-ui.html
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/wallets` | Create a new wallet |
| GET | `/api/wallets?email={email}` | Get wallet by email |
| POST | `/api/wallets/{id}/bank-accounts` | Link bank account to wallet |
| GET | `/api/wallets/{id}/bank-accounts` | Get all linked bank accounts |
| POST | `/api/wallets/{id}/fund` | Fund wallet via payment gateway |
| GET | `/api/wallets/{id}/transactions` | Get wallet transaction history |

## Architecture

### Design Patterns
- **Strategy Pattern**: Payment gateway selection (Flutterwave/Paystack)
- **Factory Pattern**: Payment strategy instantiation
- **Repository Pattern**: Data access layer
- **DTO Pattern**: Request/response separation from entities

### Payment Processing
- Pessimistic locking prevents concurrent transaction conflicts
- `@Transactional` ensures atomic operations
- Failed payments automatically rollback all changes
- 10% simulated failure rate for testing rollback mechanism

## Testing

```
Tests: 26 total
├── WalletServiceTest: 6 tests
├── BankAccountServiceTest: 7 tests
├── PaymentServiceTest: 7 tests (includes extensibility test)
├── PalindromeCheckerTest: 6 tests
└── Integration test: 1 test

Result: ✅ All passing
```

## Extensibility

Adding new payment gateways requires:
1. Add new enum value to `PaymentGateway`
2. Create new strategy implementing `PaymentGatewayStrategy`
3. Update `PaymentGatewayFactory`
4. **No changes to `PaymentService` required!**