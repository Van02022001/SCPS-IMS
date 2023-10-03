package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.CreateSupplierForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateSupplierForm;
import com.example.sparepartsinventorymanagement.entities.Supplier;
import com.example.sparepartsinventorymanagement.repository.SupplierRepository;
import com.example.sparepartsinventorymanagement.service.SupplierService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierServiceImpl implements SupplierService {
    @Autowired
    private SupplierRepository supplierRepository;

    @Override
    public ResponseEntity<?> createSupplier(CreateSupplierForm form) {
        Supplier supplier = Supplier.builder()
                .code(form.getCode())
                .name(form.getName())
                .phone(form.getPhone())
                .email(form.getEmail())
                .taxCode(form.getTaxCode())
                .address(form.getAddress())

                .build();
        supplierRepository.save(supplier);
        return ResponseEntity.ok().body(new ResponseObject(
                HttpStatus.CREATED.toString(), "Create supplier successfully!", supplier
        ));
    }

    @Override
    public ResponseEntity<?> getAllSuppliers() {
        List<Supplier> suppliers = supplierRepository.findAll();
        if(!suppliers.isEmpty()){
            return ResponseEntity.ok().body(new ResponseObject(
                    HttpStatus.OK.toString(), "Get list Supplier successfully!", suppliers
            ));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                HttpStatus.NOT_FOUND.toString(), "List suppliers is empty!", null
        ));
    }

    @Override
    public ResponseEntity<?> getSupplierById(Long id) {
        Optional<Supplier> supplierOpt = supplierRepository.findById(id);
        if(!supplierOpt.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(), "Customer is not found!", supplierOpt
            ));
        }
        return ResponseEntity.ok().body(new ResponseObject(
                HttpStatus.OK.toString(), "Get supplier successfully!", supplierOpt.get()
        ));
    }

    @Override
    public ResponseEntity<?> updateSupplier(Long id, UpdateSupplierForm form) {
        Optional<Supplier> supplierOpt = supplierRepository.findById(id);
        if(supplierOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(), "Supplier is not found!", null
            ));
        }

        Supplier existingSupplier = supplierOpt.get();

        // Ensure we don't overwrite the existing Supplier's ID with a new one.
        Supplier updatedSupplier = Supplier.builder()
                .id(existingSupplier.getId()) // Ensure the ID remains the same
                .code(form.getCode() != null ? form.getCode() : existingSupplier.getCode())
                .name(form.getName() != null ? form.getName() : existingSupplier.getName())
                .phone(form.getPhone() != null ? form.getPhone() : existingSupplier.getPhone())
                .email(form.getEmail() != null ? form.getEmail() : existingSupplier.getEmail())
                .taxCode(form.getTaxCode() != null ? form.getTaxCode() : existingSupplier.getTaxCode())
                .address(form.getAddress() != null ? form.getAddress() : existingSupplier.getAddress())
                .build();

        supplierRepository.save(updatedSupplier);

        return ResponseEntity.ok().body(new ResponseObject(
                HttpStatus.OK.toString(), "Updated supplier successfully!", updatedSupplier
        ));
    }

    @Override
    public ResponseEntity<?> deleteSupplierById(Long id) {
        if(!supplierRepository.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(), "Supplier is not found!", null
            ));
        }
        supplierRepository.deleteSupplierById(id);
        return ResponseEntity.ok().body(new ResponseObject(
                HttpStatus.OK.toString(), "Deleted supplier successfully!", null
        ));
    }
}
