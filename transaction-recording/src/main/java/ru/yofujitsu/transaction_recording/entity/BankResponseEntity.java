package ru.yofujitsu.transaction_recording.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Embeddable
@EqualsAndHashCode
public class BankResponseEntity {

    private String bankTransactionId;
    private String reason;
}
