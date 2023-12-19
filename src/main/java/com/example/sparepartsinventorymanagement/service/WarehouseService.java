package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.WarehouseFormRequest;
import com.example.sparepartsinventorymanagement.dto.response.InventoryStaffDTO;
import com.example.sparepartsinventorymanagement.dto.response.WarehouseDTO;

import java.util.List;

public interface WarehouseService {
    List<WarehouseDTO> getAll();
    WarehouseDTO getWarehouseById(Long id);
    List<WarehouseDTO> getWarehousesByActiveStatus();
    List<WarehouseDTO> getWarehouseByName(String keyword);
    WarehouseDTO createWarehouse(WarehouseFormRequest form);
    WarehouseDTO updateWarehouse(Long id,WarehouseFormRequest form);
   // ResponseEntity<?> updateWarehouseStatus(Long id, WarehouseStatus status);

    List<InventoryStaffDTO> getAllInventoryStaffByWarehouseId(Long warehouseId);
    List<WarehouseDTO> getWarehousesExceptCurrentWarehouse();
}
