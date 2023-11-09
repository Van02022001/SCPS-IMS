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
    public void notifyCustomerRequest(Long receiptId, Long managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new NotFoundException("Manager not found"));

        NotificationTemplate template = notificationTemplateRepository.findByType(NotificationType.YEU_CAU_XUAT_KHO)
                .orElseThrow(() -> new NotFoundException("Notification template not found"));

        Date currentTime = new Date();

        //Tao thong bao moi

        Notification notification = Notification.builder()
                .user(manager)
                .sourceId(receiptId)
                .sourceType(template.getSourceType())
                .type(NotificationType.YEU_CAU_XUAT_KHO)
                .seen(false)
                .trash(false)
                .createdAt(currentTime)
                .updatedAt(currentTime)
                .content(template.getContent().replace("{requestId}", receiptId.toString()))
                .eventType(EventType.REQUEST_CREATED)
                .build();
        notification = notificationRepository.save(notification);

        //Gui thong bao qua WebSocket
        messagingTemplate.convertAndSendToUser(manager.getUsername(), "/topic/notification", notification);
    }
}
