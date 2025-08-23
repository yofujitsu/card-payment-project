package ru.yofujitsu.logging_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yofujitsu.logging_service.dto.LogEntryDto;
import ru.yofujitsu.logging_service.dto.LogLevel;
import ru.yofujitsu.logging_service.entity.LogEntryEntity;
import ru.yofujitsu.logging_service.mapper.LoggingMapper;
import ru.yofujitsu.logging_service.model.LogEntry;
import ru.yofujitsu.logging_service.repository.LoggingRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LoggingServiceTest {

    @Mock
    private LoggingRepository loggingRepository;

    @Mock
    private LoggingMapper loggingMapper;

    @InjectMocks
    private LoggingService loggingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Тест проверяет сохранение лога в базу данных через сервис.
     * Ожидается, что репозиторий вызывается с преобразованной сущностью.
     */
    @Test
    void saveLog_shouldSaveEntity() {
        LogEntryDto dto = new LogEntryDto(
                OffsetDateTime.now(), LogLevel.INFO, "Test message", "test-service"
        );
        LogEntryEntity entity = new LogEntryEntity();
        entity.setId(UUID.randomUUID());
        entity.setLevel(LogLevel.INFO);

        when(loggingMapper.toLogEntryEntity(dto)).thenReturn(entity);

        loggingService.saveLog(dto);

        verify(loggingRepository, times(1)).save(entity);
    }

    /**
     * Тест проверяет выборку логов по уровню.
     * Ожидается, что найденные сущности преобразуются в DTO LogEntry.
     */
    @Test
    void getLogsByLevel_shouldReturnMappedLogs() {
        LogEntryEntity entity = new LogEntryEntity();
        entity.setLevel(LogLevel.ERROR);
        entity.setMessage("error happened");
        entity.setService("test-service");

        LogEntry dto = new LogEntry();
        dto.setLevel("ERROR");
        dto.setMessage("error happened");

        when(loggingRepository.findAllByLevel(LogLevel.ERROR)).thenReturn(List.of(entity));
        when(loggingMapper.toLogEntry(entity)).thenReturn(dto);

        List<LogEntry> result = loggingService.getLogsByLevel("ERROR");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getMessage()).isEqualTo("error happened");
        verify(loggingRepository, times(1)).findAllByLevel(LogLevel.ERROR);
    }
}