package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.CreateCustomerForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateCustomerForm;
import com.example.sparepartsinventorymanagement.dto.response.CustomerDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CustomerService {

    ResponseEntity<?> createCustomer(CreateCustomerForm form);
    List<CustomerDTO> getAllCustomerDTOs();
    CustomerDTO  getCustomerById(Long id);
    CustomerDTO updateCustomer(Long id, UpdateCustomerForm form);
    //ResponseEntity<?> updateCustomer(Long id, UpdateCustomerForm form);

    ResponseEntity<?> updateCustomerStatus(Long id);
}
