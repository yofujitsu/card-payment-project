package ru.it_one.yofujitsu.card_validation.rabbit;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import ru.it_one.yofujitsu.card_validation.dto.log_entry.LogEntryDto;
import ru.it_one.yofujitsu.card_validation.dto.log_entry.LogLevel;

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
                    "card-validation-service"
            ));
        } catch (AmqpException e) {
            log.error(e.getMessage(), e);
        }
    }
}
