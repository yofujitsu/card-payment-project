package ru.yofujitsu.logging_service.dto;

import java.time.OffsetDateTime;

public record LogEntryDto(
        OffsetDateTime timestamp,
        LogLevel level,
        String message,
        String service
) {
}
