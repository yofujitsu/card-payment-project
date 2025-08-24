package ru.yofujitsu.transaction_recording.dto.log_entry;

import java.time.OffsetDateTime;

public record LogEntryDto(
        OffsetDateTime timestamp,
        LogLevel level,
        String message,
        String service
) {
}

