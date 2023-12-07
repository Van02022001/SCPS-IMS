package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.CreateCustomerForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateCustomerForm;
import com.example.sparepartsinventorymanagement.dto.response.CustomerDTO;
import com.example.sparepartsinventorymanagement.entities.Customer;
import com.example.sparepartsinventorymanagement.entities.CustomerType;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.CustomerRepository;
import com.example.sparepartsinventorymanagement.service.CustomerService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final EntityManager entityManager;
    @Override
    public ResponseEntity<?> createCustomer(CreateCustomerForm form) {
        if(form.getType() == null){
            CustomerType[] types = CustomerType.values();
            return ResponseEntity.badRequest().body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(), "Please select a customer type!", types
            ));
        }

        if (customerRepository.existsByEmail(form.getEmail())) {
            return ResponseEntity.badRequest().body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(), "Email already in use!", null));
        }

        if (customerRepository.existsByPhone(form.getPhone())) {
            return ResponseEntity.badRequest().body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(), "Phone number already in use!", null));
        }

        if (customerRepository.existsByTaxCode(form.getTaxCode())) {
            return ResponseEntity.badRequest().body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(), "Tax code already in use!", null));
        }


        Customer customer = Customer.builder()
                .code(generateRandomCustomerCode())
                .name(form.getName())
                .phone(form.getPhone())
                .email(form.getEmail())
                .taxCode(form.getTaxCode())
                .description(form.getDescription())
                .type(form.getType())
                .createdAt(new Date())
                .address(form.getAddress())
                .status(true)
                .build();
        customerRepository.save(customer);
        return ResponseEntity.ok().body(new ResponseObject(
                HttpStatus.CREATED.toString(), "Create customer successfully!", customer
        ));
    }

    @Override
    public List<CustomerDTO> getAllCustomerDTOs() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream().map(this::convertToCustomerDTO).collect(Collectors.toList());
    }
    private CustomerDTO convertToCustomerDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setCustomerId(customer.getId());
        dto.setAddress(customer.getAddress());
        dto.setCode(customer.getCode());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setDescription(customer.getDescription());
        dto.setEmail(customer.getEmail());
        dto.setName(customer.getName());
        dto.setPhone(customer.getPhone());
        dto.setStatus(customer.isStatus());
        dto.setTaxCode(customer.getTaxCode());
        dto.setCustomerType(customer.getType().toString());
        dto.setUpdatedAt(customer.getUpdatedAt());
        return dto;
    }
    @Override
    public CustomerDTO  getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + id));

        return convertToCustomerDTO(customer);
    }
    @Override
    @Transactional
    public CustomerDTO updateCustomer(Long id, UpdateCustomerForm form) {
        // Fetch the existing customer
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + id));

        // Update the customer's fields
        customer.setName(form.getName());
        customer.setPhone(form.getPhone());
        customer.setEmail(form.getEmail());
        customer.setTaxCode(form.getTaxCode());
        customer.setAddress(form.getAddress());
        customer.setType(form.getType());
        customer.setDescription(form.getDescription());
        customer.setUpdatedAt(new Date()); // Assuming you want to set the update time

        // Save the updated customer
        Customer updatedCustomer = customerRepository.save(customer);

        // Convert the updated entity to DTO and return it
        return convertToCustomerDTO(updatedCustomer);
    }


    @Override
    public ResponseEntity<?> updateCustomerStatus(Long id) {
        Customer customer = customerRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Customer not found"));

        if(customer.isStatus()){
            customer.setStatus(false);
        } else{
            customer.setStatus(true);
        }

        customerRepository.save(customer);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Update customer status successfully!", null
        ));
    }

    private boolean isCustomerCodeExist(String code) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(c) FROM Customer c WHERE c.code = :code", Long.class)
                .setParameter("code", code)
                .getSingleResult();
        return count > 0;
    }

    public String generateRandomCustomerCode() {
        String code;
        do {
            Random random = new Random();
            int randomNumber = 100 + random.nextInt(900); // generates a number between 100 and 999
            code = "C" + randomNumber;
        } while (isCustomerCodeExist(code));
        return code;
    }



}
