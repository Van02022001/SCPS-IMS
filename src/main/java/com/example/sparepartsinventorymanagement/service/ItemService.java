package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.ItemFormRequest;
import com.example.sparepartsinventorymanagement.dto.response.ItemDTO;
import com.example.sparepartsinventorymanagement.entities.ItemStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ItemService {
    List<ItemDTO> getAll();
    ItemDTO getItemById(Long id);
    List<ItemDTO> getItemBySubCategory(Long productId);
    List<ItemDTO> getItemByActiveStatus();
    ItemDTO createItem(ItemFormRequest form);
    ItemDTO updateItem(Long id, ItemFormRequest form);
    ItemDTO updateItemStatus(Long id, ItemStatus status);

    List<ItemDTO> findBySubCategory_NameContainingIgnoreCase(String name);
}
