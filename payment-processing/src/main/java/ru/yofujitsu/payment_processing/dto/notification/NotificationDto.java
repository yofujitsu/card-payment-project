package ru.yofujitsu.payment_processing.dto.notification;

import ru.yofujitsu.payment_processing.dto.bank_gateway.PaymentStatus;

public record NotificationDto(
        String transactionId,
        PaymentStatus status,
        String email
) {
}
