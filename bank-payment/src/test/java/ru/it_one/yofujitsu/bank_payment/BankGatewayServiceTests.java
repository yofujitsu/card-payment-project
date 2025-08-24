package ru.it_one.yofujitsu.bank_payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.it_one.yofujitsu.bank_payment.model.BankRequest;
import ru.it_one.yofujitsu.bank_payment.model.BankResponse;
import ru.it_one.yofujitsu.bank_payment.rabbit.LogMessageProducer;
import ru.it_one.yofujitsu.bank_payment.service.BankGatewayService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class BankGatewayServiceTests {

    private BankGatewayService bankGatewayService;

    @Mock
    private LogMessageProducer logMessageProducer;

    @BeforeEach
    void setUp() {
        bankGatewayService = new BankGatewayService(logMessageProducer);
        ReflectionTestUtils.setField(bankGatewayService, "maxPaymentAmount", 10000);
    }

    /**
     * Тест проверяет формирование
     * положительного результата авторизации {@link BankResponse}
     * при запросе с допустимой суммой платежа
     */
    @Test
    void authorizeRequest_shouldApprove_whenAmountIsLessThanMax() {
        BankRequest request = new BankRequest();
        request.setAmount(BigDecimal.valueOf(5000));

        BankResponse response = bankGatewayService.authorizeRequest(request);

        assertTrue(response.getApproved());
        assertEquals("Платеж авторизован." ,response.getReason());
        assertNotNull(response.getBankTransactionId());
    }

    /**
     * Тест проверяет формирование
     * отрицательного результата авторизации {@link BankResponse}
     * при запросе с недопустимой суммой платежа
     */
    @Test
    void authorizeRequest_shouldDecline_whenAmountIsMoreThanMax() {
        BankRequest request = new BankRequest();
        request.setTransactionId(String.valueOf(UUID.randomUUID()));
        request.setAmount(BigDecimal.valueOf(20000));

        BankResponse response = bankGatewayService.authorizeRequest(request);

        assertFalse(response.getApproved());
        assertTrue(response.getReason().contains("превышает допустимое значение"));
        assertNotNull(response.getBankTransactionId());
    }
}
