package ru.it_one.yofujitsu.bank_payment.dto.bank_auth;

import java.math.BigDecimal;

public record BankAuthorizationRequest(
        String transactionId,
        String cardNumber,
        String expiryDate,
        String cvv,
        BigDecimal amount,
        String currency,
        String merchantId,
        String email
) {
}