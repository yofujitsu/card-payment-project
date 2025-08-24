package ru.yofujitsu.notification_service.dto;

public record NotificationResponseDto(
        boolean success,
        String message
) {
}
