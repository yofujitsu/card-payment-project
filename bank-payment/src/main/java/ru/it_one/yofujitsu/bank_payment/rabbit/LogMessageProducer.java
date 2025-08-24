package ru.it_one.yofujitsu.bank_payment.rabbit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.it_one.yofujitsu.bank_payment.dto.log_entry.LogEntryDto;
import ru.it_one.yofujitsu.bank_payment.dto.log_entry.LogLevel;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${logging.queue.name}")
    private String logQueueName;

    public void produce(LogLevel level, String message) {

        rabbitTemplate.convertAndSend(logQueueName, new LogEntryDto(
                OffsetDateTime.now(),
                level,
                message,
                "bank-gateway"
        ));
    }

}
