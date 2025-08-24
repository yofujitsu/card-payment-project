package ru.yofujitsu.logging_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yofujitsu.logging_service.api.LogsApi;
import ru.yofujitsu.logging_service.mapper.LoggingMapper;
import ru.yofujitsu.logging_service.model.LogEntry;
import ru.yofujitsu.logging_service.service.LoggingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LoggingController implements LogsApi {

    private final LoggingService loggingService;
    private final LoggingMapper loggingMapper;

    @Override
    public ResponseEntity<List<LogEntry>> logsGet(
            @Valid @RequestParam(value = "level", required = false) @Nullable String level
    ) {
        return ResponseEntity.ok(loggingService.getLogsByLevel(level));
    }

    @Override
    public ResponseEntity<Void> logsPost(@Valid @RequestBody LogEntry logEntry) {
        loggingService.saveLog(loggingMapper.toLogEntryDto(logEntry));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
