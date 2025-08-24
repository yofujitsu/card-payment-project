package ru.yofujitsu.logging_service.mapper;

import org.mapstruct.Mapper;
import ru.yofujitsu.logging_service.dto.LogEntryDto;
import ru.yofujitsu.logging_service.entity.LogEntryEntity;
import ru.yofujitsu.logging_service.model.LogEntry;

@Mapper(componentModel = "spring")
public interface LoggingMapper {

    LogEntryEntity toLogEntryEntity(LogEntryDto logEntryDto);

    LogEntryDto toLogEntryDto(LogEntry logEntry);

    LogEntry toLogEntry(LogEntryEntity logEntryEntity);
}
