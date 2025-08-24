package ru.yofujitsu.notification_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import ru.yofujitsu.notification_service.dto.NotificationRequestDto;
import ru.yofujitsu.notification_service.dto.NotificationResponseDto;
import ru.yofujitsu.notification_service.dto.PaymentStatus;
import ru.yofujitsu.notification_service.dto.log_entry.LogLevel;
import ru.yofujitsu.notification_service.mapper.NotificationMapper;
import ru.yofujitsu.notification_service.model.NotificationResponse;
import ru.yofujitsu.notification_service.rabbit.LogMessageProducer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private LogMessageProducer logMessageProducer;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationService notificationService;

    private NotificationRequestDto validDto;
    private NotificationResponseDto successResponseDto;
    private NotificationResponseDto errorResponseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validDto = new NotificationRequestDto("550e8400-e29b-41d4-a716-446655440000", PaymentStatus.APPROVED, "test@example.com");
        successResponseDto = new NotificationResponseDto(true, "Платеж 550e8400-e29b-41d4-a716-446655440000 успешно завершен.");
        errorResponseDto = new NotificationResponseDto(false, "Введен невалидный email: invalid-email, для транзакции с ID: 550e8400-e29b-41d4-a716-446655440000");
    }

    /**
     * Проверяет отправку email для успешной транзакции с валидным email.
     */
    @Test
    void testSendEmail_ValidEmail_Success() {
        when(notificationMapper.toNotificationResponse(successResponseDto)).thenReturn(new NotificationResponse());

        NotificationResponse response = notificationService.sendEmail(validDto);

        verify(mailSender).send(any(SimpleMailMessage.class));
        verify(logMessageProducer).produce(eq(LogLevel.INFO), eq("Письмо со статусом транзакции 550e8400-e29b-41d4-a716-446655440000 отправлено на почту test@example.com"));
        verify(notificationMapper).toNotificationResponse(eq(successResponseDto));
        assertNotNull(response);
    }

    /**
     * Проверяет отправку email для неуспешной транзакции с валидным email.
     */
    @Test
    void testSendEmail_ValidEmail_Failure() {
        NotificationRequestDto dto = new NotificationRequestDto("550e8400-e29b-41d4-a716-446655440000", PaymentStatus.REJECTED, "test@example.com");
        NotificationResponseDto failureResponseDto = new NotificationResponseDto(false, "Во время обработки платежа 550e8400-e29b-41d4-a716-446655440000 возникла ошибка. Попробуйте ещё раз.");
        when(notificationMapper.toNotificationResponse(failureResponseDto)).thenReturn(new NotificationResponse());

        NotificationResponse response = notificationService.sendEmail(dto);

        verify(mailSender).send(any(SimpleMailMessage.class));
        verify(logMessageProducer).produce(eq(LogLevel.INFO), eq("Письмо со статусом транзакции 550e8400-e29b-41d4-a716-446655440000 отправлено на почту test@example.com"));
        verify(notificationMapper).toNotificationResponse(eq(failureResponseDto));
        assertNotNull(response);
    }

    /**
     * Проверяет обработку невалидного email.
     */
    @Test
    void testSendEmail_InvalidEmail() {
        NotificationRequestDto invalidDto = new NotificationRequestDto("550e8400-e29b-41d4-a716-446655440000", PaymentStatus.APPROVED, "invalid-email");
        when(notificationMapper.toNotificationResponse(errorResponseDto)).thenReturn(new NotificationResponse());

        NotificationResponse response = notificationService.sendEmail(invalidDto);

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
        verify(logMessageProducer).produce(eq(LogLevel.WARN), eq("Введен невалидный email: invalid-email, для транзакции с ID: 550e8400-e29b-41d4-a716-446655440000"));
        verify(notificationMapper).toNotificationResponse(eq(errorResponseDto));
        assertNotNull(response);
    }

    /**
     * Проверяет содержимое отправленного email для успешной транзакции.
     */
    @Test
    void testSendEmail_ValidEmail_CheckMessageContent() {
        when(notificationMapper.toNotificationResponse(successResponseDto)).thenReturn(new NotificationResponse());
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        notificationService.sendEmail(validDto);

        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals("test@example.com", message.getTo()[0]);
        assertEquals("mail@gmail.com", message.getFrom());
        assertEquals("Статус платежа 550e8400-e29b-41d4-a716-446655440000", message.getSubject());
        assertEquals("Платеж 550e8400-e29b-41d4-a716-446655440000 успешно завершен.", message.getText());
    }
}
