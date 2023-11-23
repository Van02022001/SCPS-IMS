package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.ItemMovementRequest;
import com.example.sparepartsinventorymanagement.dto.response.ItemMovementByItemDTO;
import com.example.sparepartsinventorymanagement.dto.response.ItemMovementDTO;

import java.util.List;

public interface ItemMovementService {
    List<ItemMovementDTO> getByItem(Long itemId);
    ItemMovementDTO getById(Long id);
    ItemMovementDTO createItemMovementInWarehouse(ItemMovementRequest request);
}
