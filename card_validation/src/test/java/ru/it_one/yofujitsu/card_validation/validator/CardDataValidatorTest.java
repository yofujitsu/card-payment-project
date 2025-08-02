package ru.it_one.yofujitsu.card_validation.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.it_one.yofujitsu.card_validation.dto.CardDataDto;
import ru.it_one.yofujitsu.card_validation.dto.ValidationResultDto;

import static org.junit.jupiter.api.Assertions.*;

class CardDataValidatorTest {

    private CardDataValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CardDataValidator();
    }

    /**
     * Тест проверяет, что при вводе валидных данных карты
     * формируется ответ с положительным результатом проверки
     */
    @Test
    void whenCardIsValid_thenReturnsValidResult() {
        CardDataDto validCard = new CardDataDto("1111222233334444", "12/25", "123");

        ValidationResultDto result = validator.validate(validCard);

        assertTrue(result.valid());
        assertEquals("Данные введенной карты прошли проверку.", result.message());
    }

    /**
     * Тест проверяет, что при вводе номера карты, не проходящего алгоритм Луна
     * формируется ответ с отрицательным результатом проверки
     */
    @Test
    void whenCardNumberIsInvalidByLuhn_thenReturnsInvalidResult() {
        CardDataDto invalidCard = new CardDataDto("1234123412341234", "12/25", "123");

        ValidationResultDto result = validator.validate(invalidCard);

        assertFalse(result.valid());
        assertEquals("Введен несуществующий номер карты.", result.message());
    }

    /**
     * Тест проверяет, что при вводе невалидного по формату номера карты
     * формируется ответ с отрицательным результатом проверки
     */
    @Test
    void whenCardNumberLengthIsInvalid_thenReturnsInvalidResult() {
        CardDataDto invalidCard = new CardDataDto("12345678", "12/25", "123");

        ValidationResultDto result = validator.validate(invalidCard);

        assertFalse(result.valid());
        assertEquals("Номер карты должен состоять из 16 цифр.", result.message());
    }

    /**
     * Тест проверяет, что при вводе истекшего срока истечения карты
     * формируется ответ с отрицательным результатом проверки
     */
    @Test
    void whenExpiryDateIsPast_thenReturnsInvalidResult() {
        CardDataDto expiredCard = new CardDataDto("1111222233334444", "01/25", "123");

        ValidationResultDto result = validator.validate(expiredCard);

        assertFalse(result.valid());
        assertEquals("Срок действия карты истёк.", result.message());
    }

    /**
     * Тест проверяет, что при вводе невалидного по формату срока истечения карты
     * формируется ответ с отрицательным результатом проверки
     */
    @Test
    void whenExpiryDateFormatIsInvalid_thenReturnsInvalidResult() {
        CardDataDto invalidFormatCard = new CardDataDto("1111222233334444", "2025-01", "123");

        ValidationResultDto result = validator.validate(invalidFormatCard);

        assertFalse(result.valid());
        assertEquals("Срок истечения карты должен быть в формате ММ/гг.", result.message());
    }

    /**
     * Тест проверяет, что при вводе невалидного по формату CVV
     * формируется ответ с отрицательным результатом проверки
     */
    @Test
    void whenCvvIsInvalid_thenReturnsInvalidResult() {
        CardDataDto invalidCvvCard = new CardDataDto("1111222233334444", "12/25", "12");

        ValidationResultDto result = validator.validate(invalidCvvCard);

        assertFalse(result.valid());
        assertEquals("CVV должен состоять из 3 цифр.", result.message());
    }
}
