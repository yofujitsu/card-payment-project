package ru.yofujitsu.logging_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import ru.yofujitsu.logging_service.dto.LogLevel;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "logs")
public class LogEntryEntity {

    @Id
    @UuidGenerator
    private UUID id;

    @Enumerated(EnumType.STRING)
    private LogLevel level;

    private OffsetDateTime timestamp;

    private String message;

    private String service;
}
