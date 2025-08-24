package ru.yofujitsu.logging_service.rabbit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.yofujitsu.logging_service.dto.LogEntryDto;
import ru.yofujitsu.logging_service.service.LoggingService;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogEntryConsumer {

    private final LoggingService loggingService;

    @RabbitListener(queues = "${logging.queue.name}")
    public void consume(LogEntryDto dto) {
        log.info("Получена новая запись лога: Тип: {}, Сообщение: {}, Сервис: {}", dto.level(), dto.message(), dto.service());
        loggingService.saveLog(dto);
    }
}
