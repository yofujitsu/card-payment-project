package ru.yofujitsu.payment_processing.rabbit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yofujitsu.payment_processing.dto.log_entry.LogLevel;
import ru.yofujitsu.payment_processing.dto.notification.NotificationDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationProducer {

    @Value("${notification.queue.name}")
    private String notificationQueue;

    private final RabbitTemplate rabbitTemplate;
    private final LogMessageProducer logMessageProducer;

    public void produce(NotificationDto notificationDto) {
        try{
            rabbitTemplate.convertAndSend(notificationQueue, notificationDto);
            log.info("Отправил уведомление о текущем статусе платежа: {}", notificationDto.transactionId());
        } catch (AmqpException e) {
            logMessageProducer.produce(LogLevel.ERROR, "Ошибка отправки уведомления о текущем статусе платежа %s: %s"
                    .formatted(notificationDto.transactionId(), e.getMessage()));
        }
    }
}
