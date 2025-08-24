package ru.yofujitsu.transaction_recording.dto;

import java.util.UUID;

public record BankResponseDto(
        UUID bankTransactionId,
        PaymentStatus status,
        String message
) {}

