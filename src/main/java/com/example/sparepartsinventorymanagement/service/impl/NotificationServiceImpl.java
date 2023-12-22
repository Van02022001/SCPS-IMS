package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.response.NotificationDTO;
import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.NotificationRepository;
import com.example.sparepartsinventorymanagement.repository.NotificationTemplateRepository;
import com.example.sparepartsinventorymanagement.repository.UserRepository;
import com.example.sparepartsinventorymanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {


    private final NotificationTemplateRepository notificationTemplateRepository;


    private final NotificationRepository notificationRepository;


    private final SimpMessagingTemplate messagingTemplate;

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;




    @Override
    public Notification  createAndSendNotification(SourceType sourceType, EventType eventType, Long sourceId, Long userId, NotificationType notificationType, String customMessage) {
        try {
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
        return notification;
        } catch (Exception e) {
            log.error("Error creating and sending notification: " + e.getMessage(), e);
            throw e; // Or handle this gracefully
        }
    }

    @Override
    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found"));
        notification.setSeen(true);
        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationDTO> getNotificationsForUser(Long userId, Optional<Boolean> isRead, Optional<NotificationType> type) {
        List<Notification> notifications;

        if (isRead.isPresent() && type.isPresent()) {
            notifications = notificationRepository.findByUserIdAndSeenAndType(userId, isRead, type.get());
        } else if (isRead.isPresent()) {
            notifications = notificationRepository.findByUserIdAndSeen(userId, isRead.get());
        } else if (type.isPresent()) {
            notifications = notificationRepository.findByUserIdAndType(userId, type.get());
        } else {
            notifications = notificationRepository.findByUserId(userId);
        }

        return notifications.stream()
                .map(notification -> modelMapper.map(notification, NotificationDTO.class))
                .collect(Collectors.toList());
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

    @Override
    public NotificationDTO getNotificationDetails(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found"));

        notification.setSeen(true);

        NotificationDTO dto =  modelMapper.map(notification, NotificationDTO.class);
        dto.setUserId(notification.getUser().getId());

        return dto;

    }

    @Override
    public List<NotificationDTO> getAllNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);

        return notifications.stream()
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                .map(notification -> modelMapper.map(notification, NotificationDTO.class))
                .collect(Collectors.toList());
    }

}
