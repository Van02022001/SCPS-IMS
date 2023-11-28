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



    List<InventoryDTO> getAllInventoryByWarehouse(Long warehouseId);


    List<InventoryDTO> getConsolidatedInventoryByItem();
    List<InventoryDTO> getAllInventoryForCurrentStaff();

}
