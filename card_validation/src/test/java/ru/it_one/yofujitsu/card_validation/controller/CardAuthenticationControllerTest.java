package ru.it_one.yofujitsu.card_validation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.it_one.yofujitsu.card_validation.dto.CardDataDto;
import ru.it_one.yofujitsu.card_validation.dto.ValidationResultDto;
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
     * Тест проверяет возврат результата {@link ValidationResultDto}
     * проверки валидных данных карты {@link CardDataDto}
     * @throws Exception
     */
    @Test
    void whenCardDataIsValid_thenReturnsValidResult() throws Exception {
        CardDataDto cardData = new CardDataDto("1111222233334444", "12/25", "123");
        ValidationResultDto successResult = new ValidationResultDto(true, "Данные введенной карты прошли проверку.");

        when(cardDataValidator.validate(any(CardDataDto.class)))
                .thenReturn(successResult);

        mockMvc.perform(post("/card/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardData)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(successResult)));
    }

    /**
     * Тест проверяет возврат статуса Bad Request
     * при отсутствии заполненных полей в данных карты {@link CardDataDto}
     *
     * @throws Exception
     */
    @Test
    void whenCardNumberIsBlank_thenReturnsBadRequest() throws Exception {
        CardDataDto invalidCard = new CardDataDto("", "12/25", "123");

        mockMvc.perform(post("/card/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCard)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Тест проверяет возврат результата {@link ValidationResultDto}
     * проверки невалидных данных карты {@link CardDataDto} с причиной невалидности
     * @throws Exception
     */
    @Test
    void whenCardDataIsInvalid_thenReturnsInvalidResult() throws Exception {
        CardDataDto invalidCard = new CardDataDto("1234123412341234", "12/99", "123");
        ValidationResultDto errorResult = new ValidationResultDto(false, "Введен несуществующий номер карты.");

        when(cardDataValidator.validate(any(CardDataDto.class)))
                .thenReturn(errorResult);

        mockMvc.perform(post("/card/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCard)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(errorResult)));
    }
}
