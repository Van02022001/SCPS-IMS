package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.UpdateInventoryForm;
import com.example.sparepartsinventorymanagement.dto.response.InventoryDTO;
import com.example.sparepartsinventorymanagement.dto.response.InventoryItemSummaryDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface InventoryService {
    ResponseEntity<?> getAll();
    ResponseEntity<?> getInventoryById(Long id);


    List<InventoryDTO> getAllInventoryByWarehouse(Long warehouseId);

    List<InventoryItemSummaryDTO> getInventorySummaryForAllItems();

}
