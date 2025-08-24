package ru.yofujitsu.payment_processing.dto.log_entry;

import java.time.OffsetDateTime;

public record LogEntryDto(
        OffsetDateTime timestamp,
        LogLevel level,
        String message,
        String service
) {
}
