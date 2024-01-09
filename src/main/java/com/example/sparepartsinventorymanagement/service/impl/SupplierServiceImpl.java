package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.CreateSupplierForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateSupplierForm;
import com.example.sparepartsinventorymanagement.dto.response.SuppliersDTO;
import com.example.sparepartsinventorymanagement.entities.Supplier;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.SupplierRepository;
import com.example.sparepartsinventorymanagement.service.SupplierService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
//@CacheConfig(cacheNames = "suppliersCache", cacheManager = "redisCacheManager")
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    private final ModelMapper modelMapper;
    private final EntityManager entityManager;


    @Override
    //@CachePut(key = "#result.id")
    public ResponseEntity<?> createSupplier(CreateSupplierForm form) {
        if (supplierRepository.existsByEmail(form.getEmail())) {
            return ResponseEntity.badRequest().body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(), "Email already in use!", null));
        }

        if (supplierRepository.existsByPhone(form.getPhone())) {
            return ResponseEntity.badRequest().body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(), "Phone number already in use!", null));
        }

        if (supplierRepository.existsByTaxCode(form.getTaxCode())) {
            return ResponseEntity.badRequest().body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(), "Tax code already in use!", null));
        }
        Supplier supplier = Supplier.builder()
                .code(generateRandomSupplierCode())
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


    //@Cacheable()
    @Override
    public List<SuppliersDTO> getAllSuppliers() {
        List<Supplier> suppliers = supplierRepository.findAll();

        return suppliers.stream()
                .map(supplier -> modelMapper.map(supplier, SuppliersDTO.class))
                .toList();
    }

    @Override
    //@Cacheable( key = "#id")
    public SuppliersDTO getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .map(supplier -> modelMapper.map(supplier, SuppliersDTO.class))
                .orElseThrow(() -> new NotFoundException("Supplier not found with id: " + id)); // Trả về null nếu không tìm thấy
    }



    @Override
    //@CachePut(key = "#id")
    public ResponseEntity<?> updateSupplier(Long id, UpdateSupplierForm form) {
        Optional<Supplier> supplierOpt = supplierRepository.findById(id);

        if (supplierOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(), "Supplier is not found!", null
            ));
        }

        Supplier existingSupplier = supplierOpt.get();

        // Ensure we don't overwrite the existing Supplier's ID with a new one.
        Supplier updatedSupplier = Supplier.builder()
                .code(existingSupplier.getCode())
                .id(existingSupplier.getId()) // Ensure the ID remains the same
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
   // @CacheEvict(key = "#id")
    public ResponseEntity<?> updateSupplierStatus(Long id) {

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Supplier not found"));

        if (supplier.isStatus()) {
            supplier.setStatus(false);
        } else {
            supplier.setStatus(true);
        }

        supplierRepository.save(supplier);
        return ResponseEntity.ok().body(new ResponseObject(
                HttpStatus.OK.toString(), "Update supplier status successfully!", null
        ));
    }


    private boolean isSupplierCodeExist(String code) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(s) FROM Supplier s WHERE s.code = :code", Long.class)
                .setParameter("code", code)
                .getSingleResult();
        return count > 0;
    }

    public String generateRandomSupplierCode() {
        String code;
        do {
            Random random = new Random();
            int randomNumber = 100 + random.nextInt(900); // generates a number between 100 and 999
            code = "S" + randomNumber;
        } while (isSupplierCodeExist(code));
        return code;
    }
}
