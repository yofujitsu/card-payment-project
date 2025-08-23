package ru.yofujitsu.transaction_recording.dto;

import java.math.BigDecimal;

public record FinalTransactionStatusDto(
        String transactionId,
        TransactionStatusDto status,
        BankResponseDto bankResponse,
        BigDecimal amount,
        String currency
) {
}

