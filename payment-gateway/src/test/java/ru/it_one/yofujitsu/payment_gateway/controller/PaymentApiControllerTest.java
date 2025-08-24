package ru.it_one.yofujitsu.payment_gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.it_one.yofuijtsu.payment_gateway.model.AuthorizationResult;
import ru.it_one.yofuijtsu.payment_gateway.model.PaymentRequest;
import ru.it_one.yofujitsu.payment_gateway.dto.bank_auth.PaymentStatus;
import ru.it_one.yofujitsu.payment_gateway.service.PaymentGatewayService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentApiController.class)
class PaymentApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentGatewayService paymentGatewayService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Тест проверяет, что при штатной работе метода
     * {@link PaymentGatewayService#processAuthorization(PaymentRequest)}
     * запрос вернет ответ с результатом обработки запроса со статусом 200 OK
     * @throws Exception
     */
    @Test
    void whenValidPaymentRequestIsPosted_thenReturnsOkWithAcceptedStatus() throws Exception {
        PaymentRequest request = new PaymentRequest(
                "1111222233334444", "12/25", "123",
                BigDecimal.valueOf(100.00), "RUB", "merchant-123", "mail@mail.ru");

        String transactionId = UUID.randomUUID().toString();
        AuthorizationResult mockResult = new AuthorizationResult();
        mockResult.setTransactionId(transactionId);
        mockResult.setStatus(PaymentStatus.ACCEPTED.toString());
        mockResult.setMessage("Платежный запрос принят в обработку.");

        when(paymentGatewayService.processAuthorization(any(PaymentRequest.class)))
                .thenReturn(mockResult);

        mockMvc.perform(post("/payment/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockResult)));
    }

    /**
     * Тест проверяет, что при передаче невалидного поля в {@link PaymentRequest}
     * запрос вернет ответ со статусом 400 Bad Request
     * @throws Exception
     */
    @Test
    void whenInvalidPaymentRequestIsPosted_thenReturnsBadRequest() throws Exception {
        PaymentRequest invalidRequest = new PaymentRequest(
                "", "12/25", "123",
                BigDecimal.valueOf(100.00), "RUB", "merchant-123", "mail@mail.ru");

        mockMvc.perform(post("/payment/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
