package com.pismo.controller;

import com.pismo.dto.TransactionDto;
import com.pismo.service.TransactionService;
import com.pismo.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/transactions")
@Slf4j
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ValidationService validationService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTransaction(@RequestBody TransactionDto transactionDto) {
        log.info("Received request to create transaction: {}", transactionDto);
        validationService.validateCreateTransaction(transactionDto);
        TransactionDto result = transactionService.createTransaction(transactionDto);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Transaction created successfully",
                "data", result
        ));
    }
}
