package ru.yofujitsu.payment_processing.rabbit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.yofujitsu.payment_processing.dto.bank_gateway.BankAuthorizationResponseDto;
import ru.yofujitsu.payment_processing.service.PaymentProcessingService;

@Component
@RequiredArgsConstructor
@Slf4j
public class BankPaymentAuthConsumer {

    private final PaymentProcessingService paymentProcessingService;

    @RabbitListener(queues = "${payment-authorization-response.queue.name}")
    public void consume(BankAuthorizationResponseDto response) {
        log.info("Получен ответ от банка: {}", response);
        paymentProcessingService.processBankPayment(response);
    }
}
