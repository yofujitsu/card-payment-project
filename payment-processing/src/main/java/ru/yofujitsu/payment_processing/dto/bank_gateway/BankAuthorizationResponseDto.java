package ru.yofujitsu.payment_processing.dto.bank_gateway;

import java.math.BigDecimal;
import java.util.UUID;

public record BankAuthorizationResponseDto(
        UUID transactionId,
        BankResponseDto bankResponse,
        String cardNumber,
        String expiryDate,
        String cvv,
        BigDecimal amount,
        String currency,
        String email
) {
}
