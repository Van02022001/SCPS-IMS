package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.UpdateInventoryForm;
import org.springframework.http.ResponseEntity;

public interface InventoryService {
    ResponseEntity<?> getAll();
    ResponseEntity<?> getInventoryById(Long id);

    ResponseEntity<?> updateInventoryById(Long id, UpdateInventoryForm form);

    ResponseEntity<?> deleteInventoryById(Long id);
}
