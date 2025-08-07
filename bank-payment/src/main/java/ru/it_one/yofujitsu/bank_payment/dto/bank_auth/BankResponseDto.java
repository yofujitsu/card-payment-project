package ru.it_one.yofujitsu.bank_payment.dto.bank_auth;

import java.util.UUID;

public record BankResponseDto(
        UUID bankTransactionId,
        PaymentStatus status,
        String message
) {}
