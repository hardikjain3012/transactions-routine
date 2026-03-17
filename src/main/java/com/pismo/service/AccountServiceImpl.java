package com.pismo.service;

import com.pismo.dto.AccountDto;
import com.pismo.entity.Account;
import com.pismo.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        Account account = Account.builder()
                .accountId(UUID.randomUUID().toString())
                .documentNumber(accountDto.getDocumentNumber())
                .creditLimit(accountDto.getCreditLimit())
                .build();
        try {
            account = accountRepository.save(account);
            log.info("Account created with ID: {}", account.getAccountId());
        } catch (Exception e) {
            log.error("Error creating account: {}", e.getMessage());
            throw new RuntimeException("Failed to create account");
        }
        return AccountDto.builder()
                .accountId(account.getAccountId())
                .documentNumber(account.getDocumentNumber())
                .creditLimit(account.getCreditLimit())
                .build();
    }

    @Override
    public AccountDto getAccountById(String accountId) {
        Account account = null;
        try {
            account = accountRepository.findById(accountId).orElse(null);
        } catch (Exception e) {
            log.error("Error retrieving account with ID {}: {}", accountId, e.getMessage());
            throw new RuntimeException("Failed to retrieve account");
        }
        if (account == null) {
            throw new RuntimeException("Account not found with id: " + accountId);
        }
        return AccountDto.builder()
                .accountId(account.getAccountId())
                .documentNumber(account.getDocumentNumber())
                .creditLimit(account.getCreditLimit())
                .build();
    }
}
