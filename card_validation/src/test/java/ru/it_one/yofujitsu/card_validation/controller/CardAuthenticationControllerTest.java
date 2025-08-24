package ru.it_one.yofujitsu.card_validation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.it_one.yofuijtsu.card_validation.model.CardData;
import ru.it_one.yofuijtsu.card_validation.model.ValidationResult;
import ru.it_one.yofujitsu.card_validation.validator.CardDataValidator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardAuthenticationController.class)
class CardAuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardDataValidator cardDataValidator;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Тест проверяет возврат результата {@link ValidationResult}
     * проверки валидных данных карты {@link CardData}
     * @throws Exception
     */
    @Test
    void whenCardDataIsValid_thenReturnsValidResult() throws Exception {
        CardData cardData = new CardData("1111222233334444", "12/25", "123");
        ValidationResult successResult = new ValidationResult();
        successResult.setMessage("Данные введенной карты прошли проверку.");
        successResult.setValid(true);

        when(cardDataValidator.validate(any(CardData.class)))
                .thenReturn(successResult);

        mockMvc.perform(post("/card/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardData)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(successResult)));
    }

    /**
     * Тест проверяет возврат статуса Bad Request
     * при отсутствии заполненных полей в данных карты {@link CardData}
     *
     * @throws Exception
     */
    @Test
    void whenCardNumberIsBlank_thenReturnsBadRequest() throws Exception {
        CardData invalidCard = new CardData("", "12/25", "123");

        ValidationResult errorResult = new ValidationResult();
        errorResult.setMessage("Номер карты обязателен к вводу.");
        errorResult.setValid(false);

        when(cardDataValidator.validate(any(CardData.class)))
                .thenReturn(errorResult);

        mockMvc.perform(post("/card/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCard)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(errorResult)));
    }

    /**
     * Тест проверяет возврат результата {@link ValidationResult}
     * проверки невалидных данных карты {@link CardData} с причиной невалидности
     * @throws Exception
     */
    @Test
    void whenCardDataIsInvalid_thenReturnsInvalidResult() throws Exception {
        CardData invalidCard = new CardData("1234123412341234", "12/99", "123");
        ValidationResult errorResult = new ValidationResult();
        errorResult.setMessage("Введен несуществующий номер карты.");
        errorResult.setValid(false);

        when(cardDataValidator.validate(any(CardData.class)))
                .thenReturn(errorResult);

        mockMvc.perform(post("/card/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCard)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(errorResult)));
    }
}
