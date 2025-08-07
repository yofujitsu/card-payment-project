package ru.it_one.yofujitsu.bank_payment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.it_one.yofujitsu.bank_payment.controller.BankGatewayController;
import ru.it_one.yofujitsu.bank_payment.model.BankRequest;
import ru.it_one.yofujitsu.bank_payment.model.BankResponse;
import ru.it_one.yofujitsu.bank_payment.service.BankGatewayService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = BankGatewayController.class)
class BankGatewayControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BankGatewayService bankGatewayService;

    /**
     * Тест проверяет формирование
     * положительного результата авторизации {@link BankResponse}
     * при запросе с допустимой суммой платежа и возврат статуса 200 Ok
     * @throws Exception
     */
    @Test
    void bankAuthorizePost_shouldReturnPositiveResponse() throws Exception {
        BankRequest request = new BankRequest();
        request.setAmount(BigDecimal.valueOf(5000));

        BankResponse response = new BankResponse();
        response.setApproved(true);
        response.setBankTransactionId(UUID.randomUUID().toString());

        when(bankGatewayService.authorizeRequest(any(BankRequest.class))).thenReturn(response);

        mockMvc.perform(post("/bank/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "transactionId": "887bcbdd-8fd6-4235-ae45-f38025f74d7d",
                          "cardNumber": "1234567812345678",
                          "expiryDate": "12/25",
                          "cvv": "123",
                          "amount": 5000,
                          "currency": "USD",
                          "email": "mail@gmail.com"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.approved").value(true))
                .andExpect(jsonPath("$.bankTransactionId").exists());
    }

    /**
     * Тест проверяет формирование
     * отрицательного результата авторизации {@link BankResponse}
     * при запросе с недопустимой суммой платежа и возврат статуса 200 Ok
     * @throws Exception
     */
    @Test
    void bankAuthorizePost_shouldReturnNegativeResponse() throws Exception {
        BankRequest request = new BankRequest();
        request.setAmount(BigDecimal.valueOf(15000));

        BankResponse response = new BankResponse();
        response.setApproved(false);
        response.setBankTransactionId(UUID.randomUUID().toString());
        response.setReason("Сумма платежа превышает 10000.");

        when(bankGatewayService.authorizeRequest(any(BankRequest.class))).thenReturn(response);

        mockMvc.perform(post("/bank/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "transactionId": "887bcbdd-8fd6-4235-ae45-f38025f74d7d",
                          "cardNumber": "1234567812345678",
                          "expiryDate": "12/25",
                          "cvv": "123",
                          "amount": 15000,
                          "currency": "USD",
                          "email": "email@email.com"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.approved").value(false))
                .andExpect(jsonPath("$.reason").value("Сумма платежа превышает 10000."))
                .andExpect(jsonPath("$.bankTransactionId").exists());
    }
}
