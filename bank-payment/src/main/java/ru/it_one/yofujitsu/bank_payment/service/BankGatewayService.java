package ru.it_one.yofujitsu.bank_payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.it_one.yofujitsu.bank_payment.dto.log_entry.LogLevel;
import ru.it_one.yofujitsu.bank_payment.model.BankRequest;
import ru.it_one.yofujitsu.bank_payment.model.BankResponse;
import ru.it_one.yofujitsu.bank_payment.rabbit.LogMessageProducer;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankGatewayService {

    @Value("${max.payment.amount}")
    private int maxPaymentAmount;

    private final LogMessageProducer logMessageProducer;

    /**
     * Метод авторизации платежа {@link BankRequest}
     * Если сумма платежа превышает {@link BankGatewayService#maxPaymentAmount},
     * возвращает отрицательный результат авторизации с причиной, иначе - положительный
     * @param request объект платежа {@link BankRequest}
     * @return {@link BankResponse} результат авторизации
     */
    public BankResponse authorizeRequest(BankRequest request) {
        String transactionId = generateTransactionId();

        BankResponse bankResponse = new BankResponse();
        bankResponse.setBankTransactionId(transactionId);

        if (request.getAmount().intValue() >= maxPaymentAmount) {
            bankResponse.setApproved(false);
            bankResponse.setReason("Сумма платежа с ID %s превышает допустимое значение %d. (Сумма: %.2f)"
                    .formatted(transactionId, maxPaymentAmount, request.getAmount()));
        } else {
            bankResponse.setApproved(true);
            bankResponse.setReason("Платеж авторизован.");
        }
        String logMessage = "Результат авторизации запроса к банку: ID платежа: %s, Сумма: %.2f, Статус: %s, ID транзакции банка: %s, Сообщение: %s"
                .formatted(request.getTransactionId(),
                        request.getAmount(),
                        bankResponse.getApproved(),
                        bankResponse.getBankTransactionId(),
                        bankResponse.getReason());

        logMessageProducer.produce(LogLevel.INFO, logMessage);

        return bankResponse;
    }

    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }
}
