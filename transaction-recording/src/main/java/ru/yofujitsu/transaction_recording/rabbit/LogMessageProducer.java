package ru.yofujitsu.transaction_recording.rabbit;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yofujitsu.transaction_recording.dto.log_entry.LogLevel;
import ru.yofujitsu.transaction_recording.dto.log_entry.LogEntryDto;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${logging.queue.name}")
    private String logQueueName;

    public void produce(LogLevel level, String message) {
        try {
            rabbitTemplate.convertAndSend(logQueueName, new LogEntryDto(
                    OffsetDateTime.now(),
                    level,
                    message,
                    "transaction-recording"
            ));
        } catch (AmqpException e) {
            log.error(e.getMessage(), e);
        }
    }
}

