package ru.it_one.yofujitsu.payment_gateway.dto.bank_auth;

import java.util.UUID;

public record BankAuthorizationResponse(
        UUID transactionId,
        PaymentStatus status,
        String message
) {}