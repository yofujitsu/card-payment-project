package ru.it_one.yofujitsu.card_validation.rabbit;

import dto.CardValidationRequest;
import dto.CardValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.it_one.yofuijtsu.card_validation.model.CardData;
import ru.it_one.yofujitsu.card_validation.validator.CardDataValidator;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardValidationConsumer {

    private final CardDataValidator cardDataValidator;

    @RabbitListener(queues = "${card-validation.queue.name}")
    public CardValidationResponse handleValidation(CardValidationRequest request) {

        var cardData = new CardData(
                request.cardNumber(),
                request.expiryDate(),
                request.cvv()
        );

        var result = cardDataValidator.validate(cardData);
        return new CardValidationResponse(Boolean.TRUE.equals(result.getValid()), result.getMessage());
    }
}
