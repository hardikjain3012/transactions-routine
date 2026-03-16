# transactions-routine

A small Spring Boot service that provides account creation and transaction ingestion endpoints and persists to a relational database (MySQL).

## Overview
This project exposes simple REST endpoints to create accounts and transactions. It includes basic validation and persistence using Spring Data JPA.

## Endpoints
All endpoints use JSON for request/response unless specified otherwise.

- POST /accounts
  - Description: Create a new account.
  - Request body (example):
    {
      "documentNumber": "12345678901"
    }
  - Response: 200 OK with message "Account created successfully" or 4xx for validation errors.

- GET /accounts/{accountId}
  - Description: Retrieve an account by its ID.
  - Response: 200 OK with Account DTO JSON.

- POST /transactions
  - Description: Create a new transaction for an existing account.
  - Request body (example):
    {
      "accountId": "<uuid-or-id>",
      "operationType": 1,
      "amount": 100.00
    }
  - Response: 200 OK with message "Transaction created successfully" or 4xx for validation errors.

## Database diagram
The minimal schema includes the following tables (described):

- accounts
  - account_id (PK) : string/UUID
  - document_number : varchar(11) UNIQUE
  
- transactions
  - transaction_id (PK) : string/UUID
  - account_id (FK -> accounts.id)
  - operation_type : bigint (references operation types table)
  - amount : decimal(38,2) NOT NULL
  - event_date : timestamp

- operation_types (lookup)
  - operation_type (PK) : bigint
  - description0 : varchar(255)

Foreign keys:
- transactions.account_id -> accounts.id
- transactions.operation_type -> operation_types.operation_type

## Validations
The application performs the following validation rules (implemented in `ValidationService`):

- Accounts
  - `documentNumber` is required and must be exactly 11 digits (regex: `^\\d{11}$`).

- Transactions
  - `accountId` is required and the account must exist.
  - `operationType` is required and must be a valid operation type (currently allowed values: 1..4).
  - `amount` is required and must not be zero.

These validations throw `IllegalArgumentException` for invalid input.
