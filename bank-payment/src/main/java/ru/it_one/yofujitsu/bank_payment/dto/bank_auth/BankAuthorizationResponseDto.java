package ru.it_one.yofujitsu.bank_payment.dto.bank_auth;

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
