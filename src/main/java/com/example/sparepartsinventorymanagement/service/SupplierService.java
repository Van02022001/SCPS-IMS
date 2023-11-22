package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.CreateSupplierForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateSupplierForm;
import com.example.sparepartsinventorymanagement.dto.response.SupplierDTO;
import com.example.sparepartsinventorymanagement.dto.response.SuppliersDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SupplierService {
    ResponseEntity<?> createSupplier(CreateSupplierForm form);
    List<SuppliersDTO> getAllSuppliers();
    SuppliersDTO  getSupplierById(Long id);
    ResponseEntity<?> updateSupplier(Long id, UpdateSupplierForm form);
    ResponseEntity<?> updateSupplierStatus(Long id);
}
