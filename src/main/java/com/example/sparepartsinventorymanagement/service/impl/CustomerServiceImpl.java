package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.CreateCustomerForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateCustomerForm;
import com.example.sparepartsinventorymanagement.entities.Customer;
import com.example.sparepartsinventorymanagement.entities.CustomerType;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.CustomerRepository;
import com.example.sparepartsinventorymanagement.service.CustomerService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    @Override
    public ResponseEntity<?> createCustomer(CreateCustomerForm form) {
        if(form.getType() == null){
            CustomerType[] types = CustomerType.values();
            return ResponseEntity.badRequest().body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(), "Please select a customer type!", types
            ));
        }

        Customer customer = Customer.builder()
                .code(form.getCode())
                .name(form.getName())
                .phone(form.getPhone())
                .email(form.getEmail())
                .taxCode(form.getTaxCode())
                .description(form.getDescription())
                .type(form.getType())
                .createdAt(new Date())
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

        Customer existingCustomer = customers.get();
        existingCustomer.setCode(form.getCode());
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


}
