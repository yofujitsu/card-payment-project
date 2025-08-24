package ru.it_one.yofujitsu.payment_gateway.rabbit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.it_one.yofujitsu.payment_gateway.dto.bank_auth.BankAuthorizationRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentAuthProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${payment-authorization-request.queue.name}")
    private String queue;

    public void produce(BankAuthorizationRequest request) {
        rabbitTemplate.convertAndSend(queue, request);
    }
}
