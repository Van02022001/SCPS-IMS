package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.CreateLocationForm;
import com.example.sparepartsinventorymanagement.dto.request.ItemFormRequest;
import com.example.sparepartsinventorymanagement.dto.request.UpdateItemForm;
import com.example.sparepartsinventorymanagement.entities.ItemStatus;
import org.springframework.http.ResponseEntity;

public interface ItemService {
    ResponseEntity<?> getAll();
    ResponseEntity<?> getItemById(Long id);
    ResponseEntity<?> getItemBySubCategory(Long productId);
    ResponseEntity<?> getItemByActiveStatus();
    ResponseEntity<?> createItem(ItemFormRequest form);
    ResponseEntity<?> updateItem(Long id, UpdateItemForm form);
    ResponseEntity<?> updateItemStatus(Long id, ItemStatus status);
    ResponseEntity<?> changeItemLocation(Long id, Long toLocationId);
    ResponseEntity<?> createItemLocation(Long id, CreateLocationForm form);
    ResponseEntity<?> getItemMovements(Long id);
    ResponseEntity<?> getHistoryPriceChange(Long id);

    ResponseEntity<?> findBySubCategory_NameContainingIgnoreCase(String name);
}
