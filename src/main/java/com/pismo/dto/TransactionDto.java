package com.pismo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDto {

    private String transactionId;
    private String accountId;
    private Long operationType;
    private BigDecimal amount;
    private String eventDate;
    private BigDecimal availableCreditLimit;
}
