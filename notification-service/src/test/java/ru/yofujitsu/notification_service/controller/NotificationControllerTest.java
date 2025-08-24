package ru.yofujitsu.notification_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yofujitsu.notification_service.dto.NotificationRequestDto;
import ru.yofujitsu.notification_service.dto.PaymentStatus;
import ru.yofujitsu.notification_service.mapper.NotificationMapper;
import ru.yofujitsu.notification_service.model.NotificationRequest;
import ru.yofujitsu.notification_service.model.NotificationResponse;
import ru.yofujitsu.notification_service.service.NotificationService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web MVC тесты для NotificationController.
 */
@WebMvcTest(NotificationController.class)
class NotificationControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private NotificationMapper notificationMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private NotificationRequest validRequest;
    private NotificationRequest invalidRequest;
    private NotificationRequestDto validRequestDto;
    private NotificationResponse successResponse;
    private NotificationResponse errorResponse;

    @BeforeEach
    void setUp() {
        validRequest = new NotificationRequest("550e8400-e29b-41d4-a716-446655440000", PaymentStatus.APPROVED.toString(), "test@example.com");
        invalidRequest = new NotificationRequest("550e8400-e29b-41d4-a716-446655440000", PaymentStatus.APPROVED.toString(), "invalid-email");
        validRequestDto = new NotificationRequestDto("550e8400-e29b-41d4-a716-446655440000", PaymentStatus.APPROVED, "test@example.com");
        successResponse = new NotificationResponse();
        successResponse.setSuccess(true);
        successResponse.setMessage("Платеж 550e8400-e29b-41d4-a716-446655440000 успешно завершен.");
        errorResponse = new NotificationResponse();
        errorResponse.setSuccess(false);
        errorResponse.setMessage("Введен невалидный email: invalid-email, для транзакции с ID: 550e8400-e29b-41d4-a716-446655440000");
    }

    /**
     * Проверяет успешную обработку POST-запроса с валидным NotificationRequest.
     */
    @Test
    void testNotifyPost_ValidRequest_Success() throws Exception {
        when(notificationMapper.toNotificationRequestDto(validRequest)).thenReturn(validRequestDto);
        when(notificationService.sendEmail(validRequestDto)).thenReturn(successResponse);

        mockMvc.perform(post("/notify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(successResponse)));
    }

    /**
     * Проверяет обработку POST-запроса с невалидным email.
     */
    @Test
    void testNotifyPost_InvalidEmail() throws Exception {
        NotificationRequestDto invalidRequestDto = new NotificationRequestDto("550e8400-e29b-41d4-a716-446655440000", PaymentStatus.APPROVED, "invalid-email");
        when(notificationMapper.toNotificationRequestDto(invalidRequest)).thenReturn(invalidRequestDto);
        when(notificationService.sendEmail(invalidRequestDto)).thenReturn(errorResponse);

        mockMvc.perform(post("/notify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(errorResponse)));
    }
}
