package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.ItemFormRequest;
import com.example.sparepartsinventorymanagement.entities.ItemStatus;
import org.springframework.http.ResponseEntity;

public interface ItemService {
    ResponseEntity<?> getAll();
    ResponseEntity<?> getItemById(Long id);
    ResponseEntity<?> getItemByProduct(Long productId);
    ResponseEntity<?> getItemByActiveStatus();
    ResponseEntity<?> createItem(ItemFormRequest form);
    ResponseEntity<?> updateItem(Long id, ItemFormRequest form);
    ResponseEntity<?> updateItemStatus(Long id, ItemStatus status);
}