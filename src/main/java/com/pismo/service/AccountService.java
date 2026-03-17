package com.pismo.service;

import com.pismo.dto.AccountDto;

public interface AccountService {
        AccountDto createAccount(AccountDto accountDto);
        AccountDto getAccountById(String accountId);
}
