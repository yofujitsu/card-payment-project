package ru.yofujitsu.payment_processing.dto.bank_gateway;

import java.util.UUID;

public record BankResponseDto(
        UUID bankTransactionId,
        PaymentStatus status,
        String message
) {}
