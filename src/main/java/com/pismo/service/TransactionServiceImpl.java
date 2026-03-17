package com.pismo.service;

import com.pismo.dto.TransactionDto;
import com.pismo.entity.Account;
import com.pismo.entity.OperationType;
import com.pismo.entity.Transaction;
import com.pismo.repository.AccountRepository;
import com.pismo.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    @Transactional
    public TransactionDto createTransaction(TransactionDto transactionDto) {
        if (transactionDto.getOperationType() != 4 && transactionDto.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            transactionDto.setAmount(transactionDto.getAmount().negate());
        }
        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .accountId(transactionDto.getAccountId())
                .operationType(transactionDto.getOperationType())
                .amount(transactionDto.getAmount())
                .eventDate(LocalDateTime.now())
                .build();
        try {
            Account account = accountRepository.findById(transaction.getAccountId()).orElseThrow(() -> new RuntimeException("Account not found with id"));
            BigDecimal creditLimit = account.getCreditLimit();
            if((transaction.getOperationType() >= 1 && transaction.getOperationType() <= 3) &&
                    (creditLimit.compareTo(transaction.getAmount().abs()) < 0)) {
                throw new IllegalArgumentException("Insufficient Balance");
            }
            transaction = transactionRepository.save(transaction);
            account.setCreditLimit(creditLimit.add(transaction.getAmount()));
            accountRepository.save(account);
            transactionDto.setAvailableCreditLimit(account.getCreditLimit());

            log.info("Transaction created with ID: {}", transaction.getTransactionId());
        } catch (Exception e) {
            log.error("Error creating transaction: {}", e.getMessage());
            throw new RuntimeException("Failed to create transaction: " + e.getMessage());
        }
        return TransactionDto.builder()
                .transactionId(transaction.getTransactionId())
                .accountId(transaction.getAccountId())
                .operationType(transaction.getOperationType())
                .amount(transaction.getAmount())
                .eventDate(transaction.getEventDate().toString())
                .availableCreditLimit(transactionDto.getAvailableCreditLimit())
                .build();
    }
}
