package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.CreateSupplierForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateSupplierForm;
import com.example.sparepartsinventorymanagement.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/suppliers")
public class SupplierController {
    @Autowired
    private SupplierService supplierService;
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For creating a new supplier")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createSupplier(@Valid @RequestBody CreateSupplierForm form) {
        return supplierService.createSupplier(form);
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For getting all suppliers")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllSuppliers(){
        return supplierService.getAllSuppliers();
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For getting a supplier by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSupplierById(
            @Parameter(description = "Enter supplier Id", example = "1", required = true)
            @PathVariable @NotNull Long id) {
        return supplierService.getSupplierById(id);
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For updating a supplier by id")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateSupplier(
            @Parameter(description = "Enter supplier Id to update", example = "1", required = true)
            @PathVariable @NotNull Long id,
            @Valid @RequestBody UpdateSupplierForm form) {
        return supplierService.updateSupplier(id, form);
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For deleting a supplier by id")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteSupplier(
            @Parameter(description = "Enter supplier Id to delete", example = "1", required = true)
            @PathVariable @NotNull Long id) {
        return supplierService.deleteSupplierById(id);
    }
}
