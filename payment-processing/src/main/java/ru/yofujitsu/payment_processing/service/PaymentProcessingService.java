package ru.yofujitsu.payment_processing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yofujitsu.payment_processing.dto.bank_gateway.BankAuthorizationResponseDto;
import ru.yofujitsu.payment_processing.dto.bank_gateway.PaymentStatus;
import ru.yofujitsu.payment_processing.dto.log_entry.LogLevel;
import ru.yofujitsu.payment_processing.exception.BankAuthResponseException;
import ru.yofujitsu.payment_processing.mapper.BankPaymentMapper;
import ru.yofujitsu.payment_processing.model.FinalTransactionStatus;
import ru.yofujitsu.payment_processing.model.TransactionStatus;
import ru.yofujitsu.payment_processing.rabbit.FinalPaymentStatusProducer;
import ru.yofujitsu.payment_processing.rabbit.LogMessageProducer;
import ru.yofujitsu.payment_processing.rabbit.NotificationProducer;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProcessingService {

    private static final String INVALID_BANK_PAYMENT_MESSAGE = "Пришел невалидный ответ от банка.";

    private final FinalPaymentStatusProducer finalPaymentStatusProducer;
    private final BankPaymentMapper bankPaymentMapper;
    private final LogMessageProducer logMessageProducer;
    private final NotificationProducer notificationProducer;

    /**
     * Обрабатывает финальный ответ от банка, определяет статус транзакции
     * и отправляет его в очередь.
     *
     * @param response Ответ от банка.
     * @return Финальный статус платежа.
     * @throws BankAuthResponseException если ID транзакции или статус
     * в ответе от банка не валидны.
     */
    public void processBankPayment(BankAuthorizationResponseDto response) {
        try {
            validateBankPayment(response);

            TransactionStatus status = response.bankResponse().status() == PaymentStatus.APPROVED
                    ? TransactionStatus.SUCCESS
                    : TransactionStatus.FAILED;

            FinalTransactionStatus finalStatus = new FinalTransactionStatus();
            finalStatus.setTransactionId(response.transactionId().toString());
            finalStatus.setStatus(status);
            finalStatus.setMessage(response.bankResponse().message());

            finalPaymentStatusProducer.sendFinalTransactionStatus(
                    bankPaymentMapper.toFinalTransactionStatusDto(finalStatus, response)
            );

            notificationProducer.produce(bankPaymentMapper.toNotificationDto(response));

            logMessageProducer.produce(LogLevel.INFO, "Отправлен финальный статус платежа В БД с ID %s, Статус: %s"
                    .formatted(response.transactionId(), status));

        } catch (BankAuthResponseException e) {
            logMessageProducer.produce(LogLevel.ERROR, "Не пройдена валидация респонса от банка. Ошибка: %s"
                    .formatted(e.getMessage()));
        }
    }

    private void validateBankPayment(BankAuthorizationResponseDto response) {
        if(response.transactionId() == null || response.bankResponse().status() == null)
            throw new BankAuthResponseException(INVALID_BANK_PAYMENT_MESSAGE);
    }
}
