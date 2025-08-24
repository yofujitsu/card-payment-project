package ru.yofujitsu.payment_processing.dto;

import ru.yofujitsu.payment_processing.dto.bank_gateway.BankResponseDto;
import ru.yofujitsu.payment_processing.dto.bank_gateway.TransactionStatusDto;

import java.math.BigDecimal;

public record FinalTransactionStatusDto(
        String transactionId,
        TransactionStatusDto status,
        BankResponseDto bankResponse,
        BigDecimal amount,
        String currency
) {
}
