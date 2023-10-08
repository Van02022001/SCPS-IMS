package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.entities.Notification;
import com.example.sparepartsinventorymanagement.entities.NotificationTemplate;
import com.example.sparepartsinventorymanagement.entities.NotificationType;
import com.example.sparepartsinventorymanagement.entities.User;
import com.example.sparepartsinventorymanagement.repository.NotificationRepository;
import com.example.sparepartsinventorymanagement.repository.NotificationTemplateRepository;
import com.example.sparepartsinventorymanagement.service.NotificationService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationTemplateRepository notificationTemplateRepository;

    @Autowired
    private NotificationRepository notificationRepository;
    @Override
    public ResponseEntity<?> sendPendingApprovalNotification(Long receiptId, User user) {
        Optional<NotificationTemplate> optionalTemplate= notificationTemplateRepository.findByType(NotificationType.YEU_CAU_XUAT_KHO);
        if(!optionalTemplate.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(), "Template for YEU_CAU_XUAT_KHO not found!", null
            ));
        }
        return null;
    }
}
