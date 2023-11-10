package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.CreateSupplierForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateSupplierForm;
import org.springframework.http.ResponseEntity;

public interface SupplierService {
    ResponseEntity<?> createSupplier(CreateSupplierForm form);
    ResponseEntity<?> getAllSuppliers();
    ResponseEntity<?> getSupplierById(Long id);
    ResponseEntity<?> updateSupplier(Long id, UpdateSupplierForm form);
    ResponseEntity<?> updateSupplierStatus(Long id);
}
