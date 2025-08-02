package ru.it_one.yofujitsu.card_validation.dto.log_entry;

import java.time.OffsetDateTime;

public record LogEntryDto(
        OffsetDateTime timestamp,
        LogLevel level,
        String message,
        String service
) {
}
