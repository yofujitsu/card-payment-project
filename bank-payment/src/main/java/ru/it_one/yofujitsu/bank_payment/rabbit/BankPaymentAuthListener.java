package ru.it_one.yofujitsu.bank_payment.rabbit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.it_one.yofujitsu.bank_payment.dto.bank_auth.BankAuthorizationRequest;
import ru.it_one.yofujitsu.bank_payment.dto.bank_auth.BankAuthorizationResponseDto;
import ru.it_one.yofujitsu.bank_payment.dto.bank_auth.BankResponseDto;
import ru.it_one.yofujitsu.bank_payment.dto.log_entry.LogLevel;
import ru.it_one.yofujitsu.bank_payment.mapper.BankPaymentMapper;
import ru.it_one.yofujitsu.bank_payment.model.BankRequest;
import ru.it_one.yofujitsu.bank_payment.model.BankResponse;
import ru.it_one.yofujitsu.bank_payment.service.BankGatewayService;

@Component
@RequiredArgsConstructor
@Slf4j
public class BankPaymentAuthListener {

    private final RabbitTemplate rabbitTemplate;
    private final BankGatewayService bankGatewayService;
    private final BankPaymentMapper bankPaymentMapper;
    private final LogMessageProducer logMessageProducer;

    @Value("${payment-authorization-response.queue.name}")
    private String bankPaymentAuthorizationResponseQueue;

    @RabbitListener(queues = "${payment-authorization-request.queue.name}")
    public void consume(BankAuthorizationRequest bankRequest) {
        BankRequest request = bankPaymentMapper.toBankRequestApi(bankRequest);
        BankResponse bankResponse = bankGatewayService.authorizeRequest(request);
        BankResponseDto bankResponseDto = bankPaymentMapper.toBankResponseDto(bankResponse);
        BankAuthorizationResponseDto bankAuthorizationResponse = bankPaymentMapper.toBankAuthorizationResponseDto(bankRequest, bankResponseDto);
        try {
            rabbitTemplate.convertAndSend(bankPaymentAuthorizationResponseQueue, bankAuthorizationResponse);
            var message = "Отправлен результат авторизации платежа от банка: ID транзакции банка: %s, Статус: %s, Сообщение: %s"
                    .formatted(bankAuthorizationResponse.bankResponse().bankTransactionId(),
                            bankAuthorizationResponse.bankResponse().status(),
                            bankAuthorizationResponse.bankResponse().message());
            logMessageProducer.produce(LogLevel.INFO, message);
        } catch (AmqpException e) {
            logMessageProducer.produce(LogLevel.ERROR,
                    "Ошибка при отправке результата авторизации платежа от банка: ID транзакции банка: %s, Ошибка: %s"
                    .formatted(bankAuthorizationResponse.bankResponse().bankTransactionId(), e.getMessage()));
        }
    }
}
