package com.pismo.service;

import com.pismo.dto.AccountDto;
import com.pismo.dto.TransactionDto;

public interface ValidationService {
    void validateCreateAccount(AccountDto accountDto);
    void validateCreateTransaction(TransactionDto transactionDto);
}
