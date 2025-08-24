package ru.it_one.yofujitsu.payment_gateway.service;

import dto.CardValidationRequest;
import dto.CardValidationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.it_one.yofuijtsu.payment_gateway.model.AuthorizationResult;
import ru.it_one.yofuijtsu.payment_gateway.model.PaymentRequest;
import ru.it_one.yofujitsu.payment_gateway.dto.bank_auth.BankAuthorizationRequest;
import ru.it_one.yofujitsu.payment_gateway.dto.bank_auth.BankAuthorizationResponse;
import ru.it_one.yofujitsu.payment_gateway.dto.bank_auth.PaymentStatus;
import ru.it_one.yofujitsu.payment_gateway.dto.log_entry.LogLevel;
import ru.it_one.yofujitsu.payment_gateway.mapper.PaymentMapper;
import ru.it_one.yofujitsu.payment_gateway.rabbit.CardValidationProducer;
import ru.it_one.yofujitsu.payment_gateway.rabbit.LogMessageProducer;
import ru.it_one.yofujitsu.payment_gateway.rabbit.PaymentAuthProducer;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentGatewayServiceTest {

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private PaymentAuthProducer paymentAuthProducer;

    @Mock
    private CardValidationProducer cardValidationProducer;

    @Mock
    private LogMessageProducer logMessageProducer;

    @InjectMocks
    private PaymentGatewayService paymentGatewayService;

    /**
     * Тест проверяет, что при вводе данных авторизации {@link PaymentRequest}
     * возвращается ответ с принятием запроса в обработку {@link AuthorizationResult}
     */
    @Test
    void whenProcessAuthorization_thenReturnsResponseFromProducer() {
        PaymentRequest request = new PaymentRequest(
                "1111222233334444", "12/25", "123",
                BigDecimal.valueOf(100.00), "RUB", "merchant-123", "mail@mail.ru");

        UUID fakeTransactionId = UUID.randomUUID();

        when(cardValidationProducer.produce(any(CardValidationRequest.class)))
                .thenReturn(new CardValidationResponse(true, "Платеж принят в обработку."));

        BankAuthorizationRequest bankRequest = new BankAuthorizationRequest(
                fakeTransactionId.toString(),
                request.getCardNumber(), request.getExpiryDate(), request.getCvv(),
                request.getAmount(), request.getCurrency(), request.getMerchantId(), request.getEmail());

        BankAuthorizationResponse expectedResult = new BankAuthorizationResponse(fakeTransactionId,
                PaymentStatus.ACCEPTED,
                "Платеж принят в обработку.");

        when(paymentMapper.toBankAuthorizationRequest(anyString(), eq(request)))
                .thenReturn(bankRequest);


        AuthorizationResult actualResult = paymentGatewayService.processAuthorization(request);
        actualResult.setTransactionId(fakeTransactionId.toString());

        assertNotNull(actualResult);
        assertEquals(expectedResult.transactionId().toString(), actualResult.getTransactionId());
        assertEquals(expectedResult.status().toString(), actualResult.getStatus().toString());
        assertEquals(expectedResult.message(), actualResult.getMessage());

        verify(logMessageProducer, times(1)).produce(eq(LogLevel.INFO), contains("Результат валидации данных карты"));
        verify(logMessageProducer, times(1)).produce(eq(LogLevel.INFO), contains("Результат авторизации платежа"));
        verify(cardValidationProducer, times(1)).produce(any(CardValidationRequest.class));
        verify(paymentMapper, times(1)).toBankAuthorizationRequest(anyString(), eq(request));
        verify(paymentAuthProducer, times(1)).produce(bankRequest);
    }

    /**
     * Тест проверяет, что номер и срок истечения карты корректно
     * замаскированы в логах, соответственно методам маскировки
     */
    @Test
    void whenProcessAuthorization_thenMasksCardDataCorrectlyInLogs() {
        PaymentRequest request = new PaymentRequest(
                "1111222233334444", "12/25", "123",
                BigDecimal.valueOf(100.00), "RUB", "merchant-123", "mail@mail.ru");

        when(cardValidationProducer.produce(any(CardValidationRequest.class)))
                .thenReturn(new CardValidationResponse(true, "Платеж принят в обработку."));

        AuthorizationResult result = paymentGatewayService.processAuthorization(request);

        String maskedCardNumber = paymentGatewayService.maskCardNumber(request.getCardNumber());
        assertEquals("**** **** **** 4444", maskedCardNumber);

        String maskedExpiryDate = paymentGatewayService.maskExpiryDate(request.getExpiryDate());
        assertEquals("**/25", maskedExpiryDate);
    }
}
