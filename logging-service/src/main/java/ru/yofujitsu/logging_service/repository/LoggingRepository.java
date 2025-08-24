package ru.yofujitsu.logging_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yofujitsu.logging_service.dto.LogLevel;
import ru.yofujitsu.logging_service.entity.LogEntryEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoggingRepository extends JpaRepository<LogEntryEntity, UUID> {
    List<LogEntryEntity> findAllByLevel(LogLevel level);
}
