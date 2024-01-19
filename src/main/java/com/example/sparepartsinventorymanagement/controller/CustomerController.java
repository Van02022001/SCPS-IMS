package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.CreateCustomerForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateCustomerForm;
import com.example.sparepartsinventorymanagement.dto.response.CustomerDTO;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.exception.ResourceNotFoundException;
import com.example.sparepartsinventorymanagement.service.CustomerService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/customers")
@Tag(name = "customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PreAuthorize("hasRole('ROLE_SALE_STAFF')  or hasRole('ROLE_MANAGER') ")
    @Operation(summary = "For creating  a new customer")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCustomer(@Valid @RequestBody CreateCustomerForm form) {

            return customerService.createCustomer(form);

    }

    @PreAuthorize("hasRole('ROLE_SALE_STAFF')or hasRole('ROLE_MANAGER')")
    @Operation(summary = "For getting all customers")
    @GetMapping()
        public ResponseEntity<?> getAllCustomers(){
        List<CustomerDTO> customerDTOs = customerService.getAllCustomerDTOs();
        if (customerDTOs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(), "List of customers is empty!", null
            ));
        }
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.toString(), "List of customers retrieved successfully!", customerDTOs
        ));
    }
    @PreAuthorize("hasRole('ROLE_SALE_STAFF') or hasRole('ROLE_MANAGER')")
    @Operation(summary = "For getting a customer by ID")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCustomerById(
            @Parameter(description = "Enter customer id ", example = "1", required = true)
            @PathVariable @NotNull Long id) {

        try {
            CustomerDTO customerDTO = customerService.getCustomerById(id);
            return ResponseEntity.ok(customerDTO);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(), e.getMessage(), null
            ));
        }
    }

    @PreAuthorize("hasRole('ROLE_SALE_STAFF') ")
    @Operation(summary = "For updating a customer by id")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateCustomer(
            @PathVariable Long id,
            @RequestBody @Valid UpdateCustomerForm form) {

        try {
            CustomerDTO customerDTO = customerService.updateCustomer(id, form);
            return ResponseEntity.ok(customerDTO);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during the update process: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_SALE_STAFF')")
    @Operation(summary = "For deleting a customer status by  supplier id")
    @PutMapping(value = "status/{id}")
    public ResponseEntity<?> deleteCustomer(
            @Parameter(description = "Enter customer id ", example = "1", required = true)
            @PathVariable @NotNull Long id) {

            return customerService.updateCustomerStatus(id);

    }

}
