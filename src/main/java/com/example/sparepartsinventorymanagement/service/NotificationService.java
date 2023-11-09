package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.entities.User;
import org.springframework.http.ResponseEntity;

public interface NotificationService {
    void notifyCustomerRequest(Long receiptId, Long managerId);

}
