package ru.yofujitsu.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.yofujitsu.notification_service.dto.NotificationRequestDto;
import ru.yofujitsu.notification_service.dto.NotificationResponseDto;
import ru.yofujitsu.notification_service.dto.PaymentStatus;
import ru.yofujitsu.notification_service.dto.log_entry.LogLevel;
import ru.yofujitsu.notification_service.mapper.NotificationMapper;
import ru.yofujitsu.notification_service.model.NotificationResponse;
import ru.yofujitsu.notification_service.rabbit.LogMessageProducer;

import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final LogMessageProducer logMessageProducer;
    private final JavaMailSender mailSender;
    private final NotificationMapper notificationMapper;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");

    /**
     * Отправляет email-уведомление о статусе транзакции.
     *
     * @param dto данные о транзакции
     * @return {@link NotificationResponse} ответ со статусом отправки уведомления
     */
    public NotificationResponse sendEmail(NotificationRequestDto dto) {

        if (!dto.email().matches(EMAIL_PATTERN.pattern())) {
            String invalidEmailMessage = "Введен невалидный email: %s, для транзакции с ID: %s".formatted(dto.email(), dto.transactionId());
            logMessageProducer.produce(
                    LogLevel.WARN,
                    invalidEmailMessage
            );
            return notificationMapper.toNotificationResponse(
                    new NotificationResponseDto(false,
                            invalidEmailMessage)
            );
        }

        String subject = "Статус платежа %s".formatted(dto.transactionId());

        String text = dto.status().equals(PaymentStatus.APPROVED) ?
                "Платеж " + dto.transactionId() + " успешно завершен." :
                "Во время обработки платежа " + dto.transactionId() + " возникла ошибка. Попробуйте ещё раз.";

        generateMessage(dto.email(), subject, text);

        logMessageProducer.produce(
                LogLevel.INFO,
                "Письмо со статусом транзакции %s отправлено на почту %s".formatted(dto.transactionId(), dto.email())
        );

        return notificationMapper.toNotificationResponse(new NotificationResponseDto(dto.status().equals(PaymentStatus.APPROVED), text));
    }

    private void generateMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("mail@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

}
