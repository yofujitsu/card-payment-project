package ru.yofujitsu.payment_processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yofujitsu.payment_processing.dto.FinalTransactionStatusDto;
import ru.yofujitsu.payment_processing.dto.bank_gateway.BankAuthorizationResponseDto;
import ru.yofujitsu.payment_processing.dto.bank_gateway.BankResponseDto;
import ru.yofujitsu.payment_processing.dto.bank_gateway.PaymentStatus;
import ru.yofujitsu.payment_processing.dto.log_entry.LogLevel;
import ru.yofujitsu.payment_processing.dto.notification.NotificationDto;
import ru.yofujitsu.payment_processing.exception.BankAuthResponseException;
import ru.yofujitsu.payment_processing.mapper.BankPaymentMapper;
import ru.yofujitsu.payment_processing.model.TransactionStatus;
import ru.yofujitsu.payment_processing.rabbit.FinalPaymentStatusProducer;
import ru.yofujitsu.payment_processing.rabbit.LogMessageProducer;
import ru.yofujitsu.payment_processing.rabbit.NotificationProducer;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentProcessingServiceTest {

    @Mock
    private FinalPaymentStatusProducer finalPaymentStatusProducer;

    @Mock
    private BankPaymentMapper bankPaymentMapper;

    @Mock
    private LogMessageProducer logMessageProducer;
    @Mock
    private NotificationProducer notificationProducer;

    @InjectMocks
    private PaymentProcessingService paymentProcessingService;

    /**
     * Тест проверяет:
     * <ul>
     *     <li>Если от банка приходит {@link PaymentStatus#APPROVED}, то транзакция должна быть
     *     помечена как {@link TransactionStatus#SUCCESS}.</li>
     * </ul>
     */
    @Test
    void testProcessBankPaymentApproved() {
        var response = new BankAuthorizationResponseDto(
                UUID.randomUUID(),
                new BankResponseDto(UUID.randomUUID(), PaymentStatus.APPROVED, "Одобрено"),
                "4111111111111111",
                "12/30",
                "123",
                BigDecimal.TEN,
                "RUB",
                "test@mail.com"
        );

        when(bankPaymentMapper.toFinalTransactionStatusDto(any(), any()))
                .thenReturn(mock(FinalTransactionStatusDto.class));
        when(bankPaymentMapper.toNotificationDto(any()))
                .thenReturn(mock(NotificationDto.class));

        paymentProcessingService.processBankPayment(response);

        verify(finalPaymentStatusProducer, times(1)).sendFinalTransactionStatus(any());
        verify(notificationProducer, times(1)).produce(any());
        verify(logMessageProducer, times(1)).produce(eq(LogLevel.INFO), contains("SUCCESS"));
    }

    /**
     * Тест проверяет:
     * <ul>
     *     <li>Если от банка приходит {@link PaymentStatus#REJECTED}, то транзакция должна быть
     *     помечена как {@link TransactionStatus#FAILED}.</li>
     * </ul>
     */
    @Test
    void testProcessBankPaymentRejected() {
        var response = new BankAuthorizationResponseDto(
                UUID.randomUUID(),
                new BankResponseDto(UUID.randomUUID(), PaymentStatus.REJECTED, "Отклонено"),
                "4111111111111111",
                "12/30",
                "123",
                BigDecimal.TEN,
                "RUB",
                "test@mail.com"
        );

        when(bankPaymentMapper.toFinalTransactionStatusDto(any(), any()))
                .thenReturn(mock(FinalTransactionStatusDto.class));
        when(bankPaymentMapper.toNotificationDto(any()))
                .thenReturn(mock(NotificationDto.class));

        paymentProcessingService.processBankPayment(response);

        verify(finalPaymentStatusProducer, times(1)).sendFinalTransactionStatus(any());
        verify(notificationProducer, times(1)).produce(any());
        verify(logMessageProducer, times(1)).produce(eq(LogLevel.INFO), contains("FAILED"));
    }

    /**
     * Тест проверяет:
     * <ul>
     *     <li>Если от банка приходит некорректный ответ (например, {@code transactionId == null}),
     *     то выбрасывается {@link BankAuthResponseException}.</li>
     *     <li>Лог пишется с уровнем {@link LogLevel#ERROR}.</li>
     * </ul>
     */
    @Test
    void testProcessBankPaymentInvalidResponse() {
        var response = new BankAuthorizationResponseDto(
                null,
                new BankResponseDto(UUID.randomUUID(), PaymentStatus.APPROVED, "Некорректный ID"),
                "4111111111111111",
                "12/30",
                "123",
                BigDecimal.TEN,
                "RUB",
                "test@mail.com"
        );

        paymentProcessingService.processBankPayment(response);

        verify(finalPaymentStatusProducer, never()).sendFinalTransactionStatus(any());
        verify(notificationProducer, never()).produce(any());
        verify(logMessageProducer, times(1)).produce(eq(LogLevel.ERROR), contains("Не пройдена валидация"));
    }

}
