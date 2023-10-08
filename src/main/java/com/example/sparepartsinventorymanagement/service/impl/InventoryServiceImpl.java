package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.UpdateInventoryForm;
import com.example.sparepartsinventorymanagement.dto.response.InventoryDTO;
import com.example.sparepartsinventorymanagement.entities.Inventory;
import com.example.sparepartsinventorymanagement.repository.InventoryRepository;
import com.example.sparepartsinventorymanagement.service.InventoryService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    public ResponseEntity<?> updateInventoryById(Long id, UpdateInventoryForm form) {
        return null;
    }

    @Override
    public ResponseEntity<?> deleteInventoryById(Long id) {
        return null;
    }
}
