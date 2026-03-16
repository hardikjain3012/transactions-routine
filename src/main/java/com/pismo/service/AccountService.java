package com.pismo.service;

import com.pismo.dto.AccountDto;

public interface AccountService {
        AccountDto createAccount(String documentNumber);
        AccountDto getAccountById(String accountId);
}
