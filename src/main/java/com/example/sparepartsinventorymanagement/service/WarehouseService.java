package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.WarehouseFormRequest;
import com.example.sparepartsinventorymanagement.entities.WarehouseStatus;
import org.springframework.http.ResponseEntity;

public interface WarehouseService {
    ResponseEntity<?> getAll();
    ResponseEntity<?> getWarehouseById(Long id);
    ResponseEntity<?> getWarehousesByActiveStatus();
    ResponseEntity<?> getWarehouseByName(String keyword);
    ResponseEntity<?> createWarehouse(WarehouseFormRequest form);
    ResponseEntity<?> updateWarehouse(Long id,WarehouseFormRequest form);
   // ResponseEntity<?> updateWarehouseStatus(Long id, WarehouseStatus status);
}
