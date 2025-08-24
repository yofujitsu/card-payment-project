package ru.it_one.yofujitsu.payment_gateway.service;

import dto.CardValidationRequest;
import dto.CardValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.it_one.yofuijtsu.payment_gateway.model.AuthorizationResult;
import ru.it_one.yofuijtsu.payment_gateway.model.PaymentRequest;
import ru.it_one.yofujitsu.payment_gateway.dto.bank_auth.BankAuthorizationRequest;
import ru.it_one.yofujitsu.payment_gateway.dto.bank_auth.PaymentStatus;
import ru.it_one.yofujitsu.payment_gateway.dto.log_entry.LogLevel;
import ru.it_one.yofujitsu.payment_gateway.mapper.PaymentMapper;
import ru.it_one.yofujitsu.payment_gateway.rabbit.CardValidationProducer;
import ru.it_one.yofujitsu.payment_gateway.rabbit.LogMessageProducer;
import ru.it_one.yofujitsu.payment_gateway.rabbit.PaymentAuthProducer;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class PaymentGatewayService {

    private final PaymentMapper paymentMapper;
    private final PaymentAuthProducer paymentAuthProducer;
    private final CardValidationProducer cardValidationProducer;
    private final LogMessageProducer logMessageProducer;

    private static final String VALID_RESULT_MESSAGE = "Платеж принят в обработку.";

    public AuthorizationResult processAuthorization(PaymentRequest request) {
        String transactionId = UUID.randomUUID().toString();

        BankAuthorizationRequest message = paymentMapper.toBankAuthorizationRequest(transactionId, request);

        var validationRequest = new CardValidationRequest(
                request.getCardNumber(),
                request.getExpiryDate(),
                request.getCvv()
        );

        CardValidationResponse validationResponse = cardValidationProducer.produce(validationRequest);

        var result = new AuthorizationResult();
        result.setTransactionId(transactionId);

        logMessageProducer.produce(LogLevel.INFO,
                "Результат валидации данных карты: Валидны: %s, Сообщение: %s, Номер карты: %s, Срок истечения карты: %s, CVV: %s"
                        .formatted(validationResponse.valid(),
                                validationResponse.reason(),
                                maskCardNumber(request.getCardNumber()),
                                maskExpiryDate(request.getExpiryDate()),
                                request.getCvv())
        );

        if (!validationResponse.valid()) {
            result.setStatus(PaymentStatus.REJECTED.toString());
            result.setMessage(validationResponse.reason());
            return result;
        }

        paymentAuthProducer.produce(message);

        result.setStatus(PaymentStatus.ACCEPTED.toString());
        result.setMessage(VALID_RESULT_MESSAGE);

        logMessageProducer.produce(LogLevel.INFO,
                "Результат авторизации платежа с ID: %s, статус: %s, сообщение: %s"
                        .formatted(result.getTransactionId(),
                                result.getStatus(),
                                result.getMessage()));

        return result;
    }

    String maskCardNumber(String cardNumber) {
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    String maskExpiryDate(String expiryDate) {
        return "**" + expiryDate.substring(expiryDate.length() - 3);
    }
}