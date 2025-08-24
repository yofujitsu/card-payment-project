package ru.it_one.yofujitsu.card_validation.handler;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        Instant timestamp,
        String message,
        List<String> details
) {}