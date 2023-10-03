package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.CreateCustomerForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateCustomerForm;
import org.springframework.http.ResponseEntity;

public interface CustomerService {

    ResponseEntity<?> createCustomer(CreateCustomerForm form);
    ResponseEntity<?> getAllCustomers();
    ResponseEntity<?> getCustomerById(Long id);
    ResponseEntity<?> updateCustomer(Long id, UpdateCustomerForm form);

    ResponseEntity<?> deleteCustomerById(Long id);
}
