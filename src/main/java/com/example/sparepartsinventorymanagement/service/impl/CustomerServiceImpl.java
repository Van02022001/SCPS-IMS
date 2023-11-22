package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.CreateCustomerForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateCustomerForm;
import com.example.sparepartsinventorymanagement.entities.Customer;
import com.example.sparepartsinventorymanagement.entities.CustomerType;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.CustomerRepository;
import com.example.sparepartsinventorymanagement.service.CustomerService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
    public ResponseEntity<?> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();

        if(!customers.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(), "Get list Customer successfully!", customers
            ));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
               HttpStatus.NOT_FOUND.toString(), "List customers is empty!", null
        ));
    }

    @Override
    public ResponseEntity<?> getCustomerById(Long id) {
        Optional<Customer> customers = customerRepository.findById(id);
        if(!customers.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(), "Customer is not found!", customers
            ));
        }

        return  ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Get customer successfully!", customers
        ));
    }

    @Override
    public ResponseEntity<?> updateCustomer(Long id, UpdateCustomerForm form) {
        Optional<Customer> customers = customerRepository.findById(id);
        if(!customers.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(), "Customer is not found!", null
            ));
        }
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

        Customer existingCustomer = customers.get();
        existingCustomer.setName(form.getName());
        existingCustomer.setPhone(form.getPhone());
        existingCustomer.setEmail(form.getEmail());
        existingCustomer.setTaxCode(form.getTaxCode());
        existingCustomer.setAddress(form.getAddress());
        existingCustomer.setType(form.getType());
        existingCustomer.setDescription(form.getDescription());
        existingCustomer.getCreatedAt();
        existingCustomer.setUpdatedAt(new Date());
        customerRepository.save(existingCustomer);

        customerRepository.save(existingCustomer);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Updated customer successfully!", existingCustomer
        ));

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
