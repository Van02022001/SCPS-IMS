package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.response.InventoryDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface InventoryService {
    ResponseEntity<?> getAll();



    List<InventoryDTO> getAllInventoryByWarehouse(Long warehouseId);


    List<InventoryDTO> getConsolidatedInventoryByItem();
    List<InventoryDTO> getAllInventoryForCurrentStaff();

}
