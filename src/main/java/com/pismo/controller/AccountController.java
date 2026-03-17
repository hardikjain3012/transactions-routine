package com.pismo.controller;

import com.pismo.dto.AccountDto;
import com.pismo.entity.Account;
import com.pismo.service.AccountService;
import com.pismo.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/accounts")
@Slf4j
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ValidationService validationService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createAccount(@RequestBody AccountDto accountDto) {
        log.info("Creating account with document number: {}", accountDto.getDocumentNumber());
        validationService.validateCreateAccount(accountDto);
        AccountDto result = accountService.createAccount(accountDto);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Account created successfully",
                "data", result
        ));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable String accountId) {
        log.info("Retrieving account with ID: {}", accountId);
        AccountDto accountDto = accountService.getAccountById(accountId);
        return ResponseEntity.ok(accountDto);
    }
}
