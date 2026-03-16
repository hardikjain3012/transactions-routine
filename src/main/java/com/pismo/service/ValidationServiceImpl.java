package com.pismo.service;

import com.pismo.dto.AccountDto;
import com.pismo.dto.TransactionDto;
import com.pismo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ValidationServiceImpl implements ValidationService {

    @Autowired
    private AccountRepository accountRepository;

    public void validateCreateAccount(AccountDto accountDto) {
        if (accountDto == null) {
            throw new IllegalArgumentException("Account data is required");
        }
        String documentNumber = accountDto.getDocumentNumber();
        if (documentNumber == null || documentNumber.isEmpty()) {
            throw new IllegalArgumentException("Document number is required");
        } else if (!documentNumber.matches("^\\d{11}$")) {
            throw new IllegalArgumentException("Document number must be exactly 11 digits");
        } else if (accountRepository.findByDocumentNumber(documentNumber).isPresent()) {
            throw new IllegalArgumentException("An account with this document number already exists");
        }
    }

    @Override
    public void validateCreateTransaction(TransactionDto transactionDto) {
        if (transactionDto == null) {
            throw new IllegalArgumentException("Transaction data is required");
        }
        if (transactionDto.getAccountId() == null || transactionDto.getAccountId().isEmpty()) {
            throw new IllegalArgumentException("Account ID is required");
        }
        if (transactionDto.getOperationType() == null) {
            throw new IllegalArgumentException("Operation type ID is required");
        }
        if (transactionDto.getAmount() == null || transactionDto.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Amount is required and must not be zero");
        }

        accountRepository.findById(transactionDto.getAccountId()).orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + transactionDto.getAccountId()));
        if (transactionDto.getOperationType() < 1 || transactionDto.getOperationType() > 4) {
            throw new IllegalArgumentException("Invalid operation type: " + transactionDto.getOperationType());
        }
    }
}
