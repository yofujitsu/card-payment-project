package ru.yofujitsu.transaction_recording.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import ru.yofujitsu.transaction_recording.dto.TransactionStatusDto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "transactions")
@EqualsAndHashCode
public class TransactionEntity {

    @Id
    @UuidGenerator
    private UUID databaseId;

    private String transactionId;

    private BigDecimal amount;

    private String currency;

    @Enumerated(EnumType.STRING)
    private TransactionStatusDto status;

    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Embedded
    private BankResponseEntity bankResponse;
}
