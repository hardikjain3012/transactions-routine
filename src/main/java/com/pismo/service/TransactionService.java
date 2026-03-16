package com.pismo.service;

import com.pismo.dto.TransactionDto;

public interface TransactionService {
    TransactionDto createTransaction(TransactionDto transactionDto);
}
