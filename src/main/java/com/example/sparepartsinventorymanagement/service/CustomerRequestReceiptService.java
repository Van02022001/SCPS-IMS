package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.CustomerRequestReceiptForm;
import com.example.sparepartsinventorymanagement.dto.response.CustomerRequestReceiptDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CustomerRequestReceiptService {

    CustomerRequestReceiptDTO createCustomerRequestReceipt(CustomerRequestReceiptForm form);
    CustomerRequestReceiptDTO getCustomerReceiptById(Long id);
    void  startCustomerRequestProcess(Long receiptId);
    void confirmCustomerRequestReceipt(Long receiptId);
    void cancelCustomerRequestReceipt(Long receiptId);

    List<CustomerRequestReceiptDTO> getAllCustomerRequestReceipts();

    List<CustomerRequestReceiptDTO> getAllCustomerRequestReceiptsByWarehouse();
}
