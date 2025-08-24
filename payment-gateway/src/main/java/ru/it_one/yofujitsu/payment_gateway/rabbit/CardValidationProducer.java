package ru.it_one.yofujitsu.payment_gateway.rabbit;

import dto.CardValidationRequest;
import dto.CardValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardValidationProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${card-validation.queue.name}")
    private String cardValidationQueue;

    public CardValidationResponse produce(CardValidationRequest request) {
        return (CardValidationResponse) rabbitTemplate.convertSendAndReceive(cardValidationQueue, request);
    }
}
