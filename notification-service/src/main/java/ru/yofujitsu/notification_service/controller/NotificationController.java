package ru.yofujitsu.notification_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yofujitsu.notification_service.api.NotifyApi;
import ru.yofujitsu.notification_service.mapper.NotificationMapper;
import ru.yofujitsu.notification_service.model.NotificationRequest;
import ru.yofujitsu.notification_service.model.NotificationResponse;
import ru.yofujitsu.notification_service.service.NotificationService;

@RestController
@RequiredArgsConstructor
public class NotificationController implements NotifyApi {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @Override
    public ResponseEntity<NotificationResponse> notifyPost(@Valid @RequestBody NotificationRequest notificationRequest) {
        return ResponseEntity.ok(notificationService.sendEmail(
                notificationMapper.toNotificationRequestDto(notificationRequest))
        );
    }
}
