package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.UpdateInventoryForm;
import com.example.sparepartsinventorymanagement.dto.response.InventoryDTO;
import com.example.sparepartsinventorymanagement.dto.response.InventoryItemSummaryDTO;
import com.example.sparepartsinventorymanagement.dto.response.NotificationDTO;
import com.example.sparepartsinventorymanagement.entities.Inventory;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.InventoryRepository;
import com.example.sparepartsinventorymanagement.service.InventoryService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public ResponseEntity<?> getAll() {
        List<Inventory> inventory = inventoryRepository.findAll();
        if(inventory.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(), "List of inventories not found!", null
            ));
        }
        List<InventoryDTO> response = inventory.stream()
                .map(inventory1 -> modelMapper.map(inventory, InventoryDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Get List of inventories successfully!", response
        ));

    }

    @Override
    public ResponseEntity<?> getInventoryById(Long id) {
        Optional<Inventory> inventory = inventoryRepository.findById(id);
        if(!inventory.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(), "Inventory not found!", null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Get inventory successfully!", inventory
        ));

    }

    @Override
    public List<InventoryDTO> getAllInventoryByWarehouse(Long warehouseId) {

        List<Inventory> inventories = inventoryRepository.findAllByWarehouseId(warehouseId);

        if (inventories.isEmpty()) {
            throw new NotFoundException("Không tìm thấy tồn kho nào cho kho với ID: " + warehouseId);
        }

        return inventories.stream()
                .map(inventory -> {
                    InventoryDTO dto =  modelMapper.map(inventory, InventoryDTO.class);
                    if (inventory.getItem() != null) {
                        dto.setItemName(inventory.getItem().getSubCategory().getName()); // Giả sử có phương thức getName() trong entity Item
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryItemSummaryDTO> getInventorySummaryForAllItems() {
        // Lấy tổng tồn kho từ tất cả các kho
        List<InventoryItemSummaryDTO> summaries = inventoryRepository.getInventorySummaryForAllItems();

        // Ánh xạ kết quả sử dụng ModelMapper (nếu cần)
        // Convert to DTO if necessary
        return summaries.stream()
                .map(summary -> modelMapper.map(summary, InventoryItemSummaryDTO.class))
                .collect(Collectors.toList());

    }
}
