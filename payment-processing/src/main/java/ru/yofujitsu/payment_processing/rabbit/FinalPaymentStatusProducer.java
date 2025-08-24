package ru.yofujitsu.payment_processing.rabbit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yofujitsu.payment_processing.dto.FinalTransactionStatusDto;
import ru.yofujitsu.payment_processing.dto.log_entry.LogLevel;

@Component
@RequiredArgsConstructor
@Slf4j
public class FinalPaymentStatusProducer {

    private final RabbitTemplate rabbitTemplate;
    private final LogMessageProducer logMessageProducer;

    @Value("${final-payment-status.queue.name}")
    private String queue;

    public void sendFinalTransactionStatus(FinalTransactionStatusDto finalTransactionStatus) {
        try {
            rabbitTemplate.convertAndSend(queue, finalTransactionStatus);
            log.info("Отправлен финальный статус платежа: {}", finalTransactionStatus);
        } catch (AmqpException e) {
            logMessageProducer.produce(LogLevel.ERROR, "Ошибка отправки финального статуса транзакции %s: %s"
                    .formatted(finalTransactionStatus.transactionId(), e.getMessage()));
        }
    }

}
