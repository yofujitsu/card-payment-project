package ru.yofujitsu.notification_service.dto;

public record NotificationRequestDto(
        String transactionId,
        PaymentStatus status,
        String email
) {
}

