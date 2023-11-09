package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.CustomerRequestReceiptForm;
import org.springframework.http.ResponseEntity;

public interface CustomerRequestReceiptService {

    ResponseEntity<?> createCustomerRequestReceipt(CustomerRequestReceiptForm form);
}
