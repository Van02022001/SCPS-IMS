package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.CreateSupplierForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateSupplierForm;
import com.example.sparepartsinventorymanagement.dto.response.SupplierDTO;
import com.example.sparepartsinventorymanagement.dto.response.SuppliersDTO;
import com.example.sparepartsinventorymanagement.service.SupplierService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/suppliers")
@RequiredArgsConstructor
@Tag(name = "supplier")

public class SupplierController {

    private final SupplierService supplierService;
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For creating a new supplier")

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createSupplier(@Valid @RequestBody CreateSupplierForm form) {
        return supplierService.createSupplier(form);
    }



    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For getting all suppliers")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> getAllSuppliers(){
        List<SuppliersDTO> suppliersDTOS = supplierService.getAllSuppliers();


        if(!suppliersDTOS.isEmpty()){
            return ResponseEntity.ok().body(new ResponseObject(
                    HttpStatus.OK.toString(), "Get list Supplier successfully!", suppliersDTOS
            ));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                HttpStatus.NOT_FOUND.toString(), "List suppliers is empty!", null
        ));
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For getting a supplier by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSupplierById(
            @Parameter(description = "Enter supplier Id", example = "1", required = true)
            @PathVariable @NotNull Long id) {
        SuppliersDTO response = supplierService.getSupplierById(id);

        if (response != null) {
            return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.toString(), "Get supplier successfully!", response));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(HttpStatus.NOT_FOUND.toString(), "Supplier not found", null));
        }
    }


    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For updating a supplier by id")

    @PutMapping(value = "{id}")
    public ResponseEntity<?> updateSupplier(
            @Parameter(description = "Enter supplier Id to update", example = "1", required = true)
            @PathVariable @NotNull Long id,
            @Valid @RequestBody UpdateSupplierForm form) {
        return supplierService.updateSupplier(id, form);
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For updating a supplier status by  supplier id")
    @PutMapping(value = "status/{id}")
    public ResponseEntity<?> updateSupplierStatus(
            @Parameter(description = "Enter supplier Id to delete", example = "1", required = true)
            @PathVariable @NotNull Long id) {
        return supplierService.updateSupplierStatus(id);
    }
}
