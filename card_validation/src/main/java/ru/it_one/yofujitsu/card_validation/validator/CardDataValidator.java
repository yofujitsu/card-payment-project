package ru.it_one.yofujitsu.card_validation.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.it_one.yofuijtsu.card_validation.model.CardData;
import ru.it_one.yofuijtsu.card_validation.model.ValidationResult;
import ru.it_one.yofujitsu.card_validation.dto.log_entry.LogLevel;
import ru.it_one.yofujitsu.card_validation.exception.ValidationException;
import ru.it_one.yofujitsu.card_validation.rabbit.LogMessageProducer;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class CardDataValidator {

    private static final String CVV_REGEXP = "^\\d{3}$";
    private static final String EXPIRY_DATE_REGEXP = "^(0[1-9]|1[0-2])/\\d{2}$";
    private static final String CARD_NUMBER_REGEXP = "^[0-9]{16}$";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/yy");

    private final LogMessageProducer logMessageProducer;

    /**
     * Метод общей валидации данных карты
     *
     * @param card дто данных карты
     * @return {@link ValidationResult} дто результата проверки
     */
    public ValidationResult validate(CardData card) {
        var validationResult = new ValidationResult();
        try {
            validateCardNumber(card.getCardNumber());
            validateExpireDate(card.getExpiryDate());
            validateCVV(card.getCvv());
        } catch (ValidationException e) {
            validationResult.setValid(false);
            validationResult.setMessage(e.getMessage());
            return validationResult;
        }
        validationResult.setValid(true);
        validationResult.setMessage("Данные введенной карты прошли проверку.");

        logMessageProducer.produce(LogLevel.INFO, "Результат валидации данных карты: **** **** **** %s, %s, %s"
                .formatted(card.getCardNumber().substring(card.getCardNumber().length() - 4),
                        validationResult.getValid(), validationResult.getMessage()));

        return validationResult;
    }

    /**
     * Метод валидации номера карты
     *
     * @param cardNumber введенный номер карты
     */
    private void validateCardNumber(String cardNumber) {
        if (cardNumber.isEmpty())
            throw new ValidationException("Номер карты обязателен к вводу.");
        if (!cardNumber.matches(CARD_NUMBER_REGEXP))
            throw new ValidationException("Номер карты должен состоять из 16 цифр.");
        if (!checkCardNumberByLuhn(cardNumber))
            throw new ValidationException("Введен несуществующий номер карты.");
    }

    /**
     * Метод проверки номера карты на соответствие алгоритму Луна
     *
     * @param cardNumber введенный номер карты
     */
    private boolean checkCardNumberByLuhn(String cardNumber) {
        int sum = 0;
        boolean alt = false;
        for (int i = cardNumber.length() - 1; i >= 0; --i) {
            int n = Character.getNumericValue(cardNumber.charAt(i));
            if (alt) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alt = !alt;
        }
        return (sum % 10 == 0);
    }

    /**
     * Метод проверки CVV
     *
     * @param cvv введенный CVV
     */
    private void validateCVV(String cvv) {
        if (cvv.isEmpty())
            throw new ValidationException("CVV обязателен к вводу.");
        if (!cvv.matches(CVV_REGEXP))
            throw new ValidationException("CVV должен состоять из 3 цифр.");
    }

    /**
     * Метод проверки срока истечения карты
     *
     * @param expiryDate введенный срок истечения карты
     */
    private void validateExpireDate(String expiryDate) {
        if (expiryDate.isEmpty())
            throw new ValidationException("Срок истечения карты обязателен к вводу.");
        if (!expiryDate.matches(EXPIRY_DATE_REGEXP))
            throw new ValidationException("Срок истечения карты должен быть в формате ММ/гг.");

        YearMonth yearMonth = YearMonth.parse(expiryDate, FORMATTER);
        YearMonth now = YearMonth.now();

        if (yearMonth.isBefore(now)) throw new ValidationException("Срок действия карты истёк.");
    }
}
