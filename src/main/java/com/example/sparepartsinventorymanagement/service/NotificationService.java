package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.response.NotificationDTO;
import com.example.sparepartsinventorymanagement.entities.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NotificationService {


    Notification  createAndSendNotification(SourceType sourceType, EventType eventType, Long sourceId, Long userId, NotificationType notificationType, String customMessage);

    void markNotificationAsRead(Long notificationId);

    List<NotificationDTO> getNotificationsForUser(Long userId, Optional<Boolean> isRead, Optional<NotificationType> type);
    void deleteNotification(Long notification);
    void markAllNotificationsAsReadForUser(Long userId);
    void restoreNotificationFromTrash(Long notificationId);


    NotificationDTO getNotificationDetails(Long notificationId);

    List<NotificationDTO> getAllNotifications(Long userId);


}
