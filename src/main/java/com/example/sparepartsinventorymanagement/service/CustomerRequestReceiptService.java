package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.CustomerRequestReceiptForm;
import com.example.sparepartsinventorymanagement.dto.response.CustomerRequestReceiptDTO;
import org.springframework.http.ResponseEntity;

public interface CustomerRequestReceiptService {

    CustomerRequestReceiptDTO createCustomerRequestReceipt(CustomerRequestReceiptForm form);
}
