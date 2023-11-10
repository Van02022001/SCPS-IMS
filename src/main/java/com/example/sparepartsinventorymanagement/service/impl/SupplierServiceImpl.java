package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.CreateSupplierForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateSupplierForm;
import com.example.sparepartsinventorymanagement.dto.response.SuppliersDTO;
import com.example.sparepartsinventorymanagement.entities.Supplier;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.SupplierRepository;
import com.example.sparepartsinventorymanagement.service.SupplierService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SupplierServiceImpl implements SupplierService {
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<?> createSupplier(CreateSupplierForm form) {
        Supplier supplier = Supplier.builder()
                .code(form.getCode())
                .name(form.getName())
                .phone(form.getPhone())
                .email(form.getEmail())
                .taxCode(form.getTaxCode())
                .address(form.getAddress())
                .createdAt(new Date())
                .updatedAt(new Date())
                .status(true)
                .build();
        Supplier createSupplier = supplierRepository.save(supplier);
        SuppliersDTO response = modelMapper.map(createSupplier, SuppliersDTO.class);

        return ResponseEntity.ok().body(new ResponseObject(
                HttpStatus.CREATED.toString(), "Create supplier successfully!", response
        ));
    }

    @Override
    public ResponseEntity<?> getAllSuppliers() {
        List<Supplier> suppliers = supplierRepository.findAll();

        List<SuppliersDTO> response = suppliers.stream()
                .map(supplier -> modelMapper.map(supplier, SuppliersDTO.class))
                .collect(Collectors.toList());
        if(!response.isEmpty()){
            return ResponseEntity.ok().body(new ResponseObject(
                    HttpStatus.OK.toString(), "Get list Supplier successfully!", response
            ));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                HttpStatus.NOT_FOUND.toString(), "List suppliers is empty!", null
        ));
    }

    @Override
    public ResponseEntity<?> getSupplierById(Long id) {
        Optional<Supplier> supplierOpt = supplierRepository.findById(id);

        if(supplierOpt.isPresent()){
            SuppliersDTO response = modelMapper.map(supplierOpt.get(), SuppliersDTO.class);
            return ResponseEntity.ok().body(new ResponseObject(
                    HttpStatus.OK.toString(), "Get supplier successfully!", response
            ));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(), "Supplier is not found!", null
            ));
        }


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
                .createdAt(existingSupplier.getCreatedAt())
                .updatedAt(new Date())
                .status(existingSupplier.isStatus())
                .build();

        Supplier supplier = supplierRepository.save(updatedSupplier);
        SuppliersDTO respone = modelMapper.map(supplier, SuppliersDTO.class);
        return ResponseEntity.ok().body(new ResponseObject(
                HttpStatus.OK.toString(), "Updated supplier successfully!", respone
        ));
    }

    @Override
    public ResponseEntity<?> updateSupplierStatus(Long id) {

        Supplier supplier = supplierRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Supplier not found"));

        if(supplier.isStatus()){
            supplier.setStatus(false);
        } else {
            supplier.setStatus(true);
        }

        supplierRepository.save(supplier);
        return ResponseEntity.ok().body(new ResponseObject(
                HttpStatus.OK.toString(), "Update supplier status successfully!", null
        ));
    }
}
