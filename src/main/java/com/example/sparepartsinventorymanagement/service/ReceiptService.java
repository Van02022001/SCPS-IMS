package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.ReceiptForm;
import org.springframework.http.ResponseEntity;

public interface ReceiptService {
    ResponseEntity<?> createReceipt(ReceiptForm receiptForm);
}
