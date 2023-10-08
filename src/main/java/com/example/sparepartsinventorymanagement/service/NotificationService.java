package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.entities.User;
import org.springframework.http.ResponseEntity;

public interface NotificationService {
    ResponseEntity<?> sendPendingApprovalNotification(Long receiptId, User user);

}
