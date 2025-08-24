package ru.yofujitsu.notification_service.rabbit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.yofujitsu.notification_service.dto.NotificationRequestDto;
import ru.yofujitsu.notification_service.service.NotificationService;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = "${notification.queue.name}")
    public void consume(NotificationRequestDto notificationRequestDto) {
        log.info("Получено уведомление о совершившейся транзакции: {}", notificationRequestDto.transactionId());
        notificationService.sendEmail(notificationRequestDto);
    }
}
