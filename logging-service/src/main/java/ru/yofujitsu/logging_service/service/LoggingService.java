package ru.yofujitsu.logging_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yofujitsu.logging_service.dto.LogEntryDto;
import ru.yofujitsu.logging_service.dto.LogLevel;
import ru.yofujitsu.logging_service.mapper.LoggingMapper;
import ru.yofujitsu.logging_service.model.LogEntry;
import ru.yofujitsu.logging_service.repository.LoggingRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoggingService {

    private final LoggingRepository loggingRepository;
    private final LoggingMapper loggingMapper;

    public void saveLog(LogEntryDto logEntryDto) {
        loggingRepository.save(loggingMapper.toLogEntryEntity(logEntryDto));
        log.info("Новая запись лога сохранена в БД: Тип: {}, Сообщение: {}, Сервис: {}",
                logEntryDto.level(), logEntryDto.message(), logEntryDto.service());
    }

    public List<LogEntry> getLogsByLevel(String level) {
        return loggingRepository.findAllByLevel(LogLevel.valueOf(level))
                .stream().map(loggingMapper::toLogEntry).toList();
    }
}
