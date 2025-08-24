package ru.yofujitsu.notification_service.mapper;

import org.mapstruct.Mapper;
import ru.yofujitsu.notification_service.dto.NotificationRequestDto;
import ru.yofujitsu.notification_service.dto.NotificationResponseDto;
import ru.yofujitsu.notification_service.model.NotificationRequest;
import ru.yofujitsu.notification_service.model.NotificationResponse;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationRequestDto toNotificationRequestDto(NotificationRequest notificationRequestDto);
    NotificationResponse toNotificationResponse(NotificationResponseDto notificationResponseDto);
}
