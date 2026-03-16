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

## Database design
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

### DDL Commands
```sql
create table accounts (
	 account_id varchar(255) primary key,
     document_number varchar(11) unique
);

create table operation_types (
	operation_type bigint primary key,
    description0 varchar(255)
);

create table transactions(
    transaction_id varchar(255) primary key,
    account_id varchar(255),
    operation_type bigint,
    amount decimal(38, 2),
    event_date timestamp,
    foreign key (account_id) references accounts(account_id),
    foreign key (operation_type) references operation_types(operation_type)
);
```

### DML for operation types

```sql
insert into operation_types(operation_type, description0) values
(1, "Normal Purchase"), 
(2, "Purchase with installments"), 
(3, "Withdrawal"), 
(4, "Credit Voucher");
```

## Validations
The application performs the following validation rules (implemented in `ValidationService`):

- Accounts
  - `documentNumber` is required and must be exactly 11 digits (regex: `^\\d{11}$`).
  - `documentNumber` must be unique (no existing account with the same document number).

- Transactions
  - `accountId` is required and the account must exist.
  - `operationType` is required and must be a valid operation type (currently allowed values: 1..4).
  - `amount` is required and must not be zero.

These validations throw `IllegalArgumentException` for invalid input.

## Running with run.bat

This repository includes a convenience script `run.bat` that builds the project and starts the service using Docker (it runs the Spring Boot app on port 8080).

Quick start (recommended so the Command Prompt stays open):

1. Open Command Prompt (cmd.exe).
2. Change directory to the project root:

   cd ./transactions-routine

3. Run the script:

   run.bat

