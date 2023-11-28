package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.UpdateInventoryForm;
import com.example.sparepartsinventorymanagement.dto.response.InventoryDTO;
import com.example.sparepartsinventorymanagement.dto.response.InventoryItemSummaryDTO;
import com.example.sparepartsinventorymanagement.dto.response.NotificationDTO;
import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.exception.InvalidResourceException;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.jwt.userprincipal.Principal;
import com.example.sparepartsinventorymanagement.repository.InventoryRepository;
import com.example.sparepartsinventorymanagement.repository.ItemRepository;
import com.example.sparepartsinventorymanagement.repository.UserRepository;
import com.example.sparepartsinventorymanagement.repository.WarehouseRepository;
import com.example.sparepartsinventorymanagement.service.InventoryService;
import com.example.sparepartsinventorymanagement.service.NotificationService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final ItemRepository itemRepository;
    private final NotificationService notificationService;
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
    public List<InventoryDTO> getConsolidatedInventoryByItem() {
        List<Inventory> inventories = inventoryRepository.findAll();

        Map<Long, InventoryDTO> sumaryMap = new HashMap<>();
        inventories.forEach(inventory -> {
            Long itemId = inventory.getItem().getId();
            InventoryDTO dto = sumaryMap.getOrDefault(itemId, new InventoryDTO());

            dto.setItemId(itemId);
            dto.setItemName(inventory.getItem().getSubCategory().getName());

            dto.setOpeningStockQuantity(dto.getOpeningStockQuantity() + inventory.getOpeningStockQuantity());
            dto.setOpeningStockValue(dto.getOpeningStockValue() + inventory.getOpeningStockValue());
            dto.setClosingStockQuantity(dto.getClosingStockQuantity() + inventory.getClosingStockQuantity());
            dto.setClosingStockValue(dto.getClosingStockValue() + inventory.getClosingStockValue());
            dto.setInboundQuantity(dto.getInboundQuantity() + inventory.getInboundQuantity());
            dto.setInboundValue(dto.getInboundValue() + inventory.getInboundValue());
            dto.setOutboundQuantity(dto.getOutboundQuantity() + inventory.getOutboundQuantity());
            dto.setOutboundValue(dto.getOutboundValue() + inventory.getOutboundValue());
            dto.setTotalValue(dto.getTotalValue() + inventory.getTotalValue());

            sumaryMap.put(itemId, dto);

        });

        return new ArrayList<>(sumaryMap.values());
    }
    @Scheduled(cron = "0 0 * * * *")
    public void checkAndNotifyLowStock() {
        List<Item> allItems = itemRepository.findAll();
        Map<Long, Integer> totalQuantities = getTotalQuantitiesByItem();
        allItems.forEach(item -> {
            int totalQuantity = totalQuantities.getOrDefault(item.getId(), 0);
            if (totalQuantity < item.getMinStockLevel()) {
                sendLowStockNotification(item);
            }
        });
    }
    @Scheduled(cron = "0 0 * * * *")
    public void checkAndNotifyHighStock(){
        List<Item> allItems = itemRepository.findAll();
        Map<Long, Integer> totalQuantities = getTotalQuantitiesByItem();
        allItems.forEach(item -> {
            int totalQuantity = totalQuantities.getOrDefault(item.getId(), 0);
            if(totalQuantity > item.getMaxStockLevel()){
                sendHighStockNotification(item);
            }
        });

    }

    private Map<Long, Integer> getTotalQuantitiesByItem() {
        List<Inventory> inventories = inventoryRepository.findAll();
        return inventories.stream()
                .collect(Collectors.groupingBy(
                        inventory -> inventory.getItem().getId(),
                        Collectors.summingInt(Inventory::getClosingStockQuantity)
                ));
    }

    private void sendLowStockNotification(Item item) {
        List<User> managers = userRepository.findUserByRole_Name("MANAGER");
        if (!managers.isEmpty()) {
            Long userId = managers.get(0).getId(); // Lấy ID của người quản lý đầu tiên

            String message = String.format("Số lượng hàng của mặt hàng %s (%s) dưới mức tối thiểu.", item.getSubCategory().getName(), item.getCode());
            notificationService.createAndSendNotification(
                    SourceType.SYSTEM,
                    EventType.STOCK_ALERT,
                    item.getId(),
                    userId, // ID người dùng cần nhận thông báo
                    NotificationType.CANH_BAO_HET_HANG,
                    message
            );
        } else {
            throw new NotFoundException("manager not found");
        }
    }

    private void sendHighStockNotification(Item item){
        List<User> managers = userRepository.findUserByRole_Name("MANAGER");
        if(!managers.isEmpty()){
            Long userId = managers.get(0).getId();

            String message = String.format("Số lượng hàng của mặt hàng %s (%s) trên mức tối đa.", item.getSubCategory().getName(), item.getCode());
            notificationService.createAndSendNotification(
                    SourceType.SYSTEM,
                    EventType.STOCK_ALERT,
                    item.getId(),
                    userId,
                    NotificationType.CANH_BAO_THUA_HANG,
                    message
            );
        }else {
            throw new NotFoundException("manager not found");
        }
    }



}
