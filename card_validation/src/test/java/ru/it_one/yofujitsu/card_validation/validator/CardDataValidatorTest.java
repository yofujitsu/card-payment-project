package ru.it_one.yofujitsu.card_validation.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.it_one.yofuijtsu.card_validation.model.CardData;
import ru.it_one.yofuijtsu.card_validation.model.ValidationResult;
import ru.it_one.yofujitsu.card_validation.rabbit.LogMessageProducer;

import static org.junit.jupiter.api.Assertions.*;

class CardDataValidatorTest {

    private CardDataValidator validator;

    @Mock
    private LogMessageProducer logMessageProducer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new CardDataValidator(logMessageProducer);
    }

    /**
     * Тест проверяет, что при вводе валидных данных карты
     * формируется ответ с положительным результатом проверки
     */
    @Test
    void whenCardIsValid_thenReturnsValidResult() {
        CardData validCard = new CardData("1111222233334444", "12/25", "123");

        ValidationResult result = validator.validate(validCard);

        assertTrue(result.getValid());
        assertEquals("Данные введенной карты прошли проверку.", result.getMessage());
    }

    /**
     * Тест проверяет, что при вводе номера карты, не проходящего алгоритм Луна
     * формируется ответ с отрицательным результатом проверки
     */
    @Test
    void whenCardNumberIsInvalidByLuhn_thenReturnsInvalidResult() {
        CardData invalidCard = new CardData("1234123412341234", "12/25", "123");

        ValidationResult result = validator.validate(invalidCard);

        assertFalse(result.getValid());
        assertEquals("Введен несуществующий номер карты.", result.getMessage());
    }

    /**
     * Тест проверяет, что при вводе невалидного по формату номера карты
     * формируется ответ с отрицательным результатом проверки
     */
    @Test
    void whenCardNumberLengthIsInvalid_thenReturnsInvalidResult() {
        CardData invalidCard = new CardData("12345678", "12/25", "123");

        ValidationResult result = validator.validate(invalidCard);

        assertFalse(result.getValid());
        assertEquals("Номер карты должен состоять из 16 цифр.", result.getMessage());
    }

    /**
     * Тест проверяет, что при вводе истекшего срока истечения карты
     * формируется ответ с отрицательным результатом проверки
     */
    @Test
    void whenExpiryDateIsPast_thenReturnsInvalidResult() {
        CardData expiredCard = new CardData("1111222233334444", "01/25", "123");

        ValidationResult result = validator.validate(expiredCard);

        assertFalse(result.getValid());
        assertEquals("Срок действия карты истёк.", result.getMessage());
    }

    /**
     * Тест проверяет, что при вводе невалидного по формату срока истечения карты
     * формируется ответ с отрицательным результатом проверки
     */
    @Test
    void whenExpiryDateFormatIsInvalid_thenReturnsInvalidResult() {
        CardData invalidFormatCard = new CardData("1111222233334444", "2025-01", "123");

        ValidationResult result = validator.validate(invalidFormatCard);

        assertFalse(result.getValid());
        assertEquals("Срок истечения карты должен быть в формате ММ/гг.", result.getMessage());
    }

    /**
     * Тест проверяет, что при вводе невалидного по формату CVV
     * формируется ответ с отрицательным результатом проверки
     */
    @Test
    void whenCvvIsInvalid_thenReturnsInvalidResult() {
        CardData invalidCvvCard = new CardData("1111222233334444", "12/25", "12");

        ValidationResult result = validator.validate(invalidCvvCard);

        assertFalse(result.getValid());
        assertEquals("CVV должен состоять из 3 цифр.", result.getMessage());
    }
}
