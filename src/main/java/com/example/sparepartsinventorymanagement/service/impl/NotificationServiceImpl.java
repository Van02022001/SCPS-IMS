package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.NotificationRepository;
import com.example.sparepartsinventorymanagement.repository.NotificationTemplateRepository;
import com.example.sparepartsinventorymanagement.repository.UserRepository;
import com.example.sparepartsinventorymanagement.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationTemplateRepository notificationTemplateRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserRepository userRepository;




    @Override
    public void createAndSendNotification(SourceType sourceType, EventType eventType, Long sourceId, Long userId, NotificationType notificationType, String customMessage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        NotificationTemplate template = notificationTemplateRepository.findByType(notificationType)
                .orElseThrow(() -> new NotFoundException("Notification template not found"));

        Notification notification = Notification.builder()
                .user(user)
                .sourceId(sourceId)
                .sourceType(sourceType)
                .eventType(eventType)
                .type(notificationType)
                .seen(false)
                .trash(false)
                .createdAt(new Date())
                .content(String.format(template.getContent(), customMessage))
                .build();

        notificationRepository.save(notification);

        // Gửi thông báo qua WebSocket
        messagingTemplate.convertAndSendToUser(user.getUsername(), "/topic/notification", notification);
    }

    @Override
    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found"));
        notification.setSeen(true);
        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getNotificationsForUser(Long userId, Optional<Boolean> isRead, Optional<NotificationType> type) {
        if(isRead.isPresent() && type.isPresent()){
            return notificationRepository.findByUserIdAndSeenAndType(userId, isRead, type.get());
        } else if(isRead.isPresent()){
            return notificationRepository.findByUserIdAndSeen(userId, isRead.get());
        } else if(type.isPresent()){
            return notificationRepository.findByUserIdAndType(userId, type.get());
        } else{
            return notificationRepository.findByUserId(userId);
        }
    }

    @Override
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found"));
        notification.setTrash(true);// Mark as trash instead of deleting
        notificationRepository.save(notification);
    }

    @Override
    public void markAllNotificationsAsReadForUser(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndSeen(userId, false);
        notifications.forEach(notification -> notification.setSeen(true));
        notificationRepository.saveAll(notifications);
    }

    @Override
    public void restoreNotificationFromTrash(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found"));
        notification.setTrash(false);
        notificationRepository.save(notification);
    }
}
