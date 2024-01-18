package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.response.InventoryDTO;
import com.example.sparepartsinventorymanagement.dto.response.WarehouseDTO;
import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.InventoryRepository;
import com.example.sparepartsinventorymanagement.repository.ItemRepository;
import com.example.sparepartsinventorymanagement.repository.UserRepository;
import com.example.sparepartsinventorymanagement.repository.WarehouseRepository;
import com.example.sparepartsinventorymanagement.service.InventoryService;
import com.example.sparepartsinventorymanagement.service.NotificationService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
                    dto.setItemId(inventory.getItem().getId());
                    dto.setItemCode(inventory.getItem().getCode());

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
            WarehouseDTO warehouseDTO = modelMapper.map(inventory.getWarehouse(), WarehouseDTO.class);
            dto.setItemId(itemId);
            dto.setItemName(inventory.getItem().getSubCategory().getName());
            dto.setItemCode(inventory.getItem().getCode());


            dto.setInboundQuantity(dto.getInboundQuantity() + inventory.getInboundQuantity());
            dto.setInboundValue(dto.getInboundValue() + inventory.getInboundValue());
            dto.setOutboundQuantity(dto.getOutboundQuantity() + inventory.getOutboundQuantity());
            dto.setOutboundValue(dto.getOutboundValue() + inventory.getOutboundValue());
            dto.setTotalValue(dto.getTotalValue() + inventory.getTotalValue());
            dto.setAvailable(dto.getAvailable() + inventory.getAvailable());
            dto.setDefective(dto.getDefective() + inventory.getDefective());
            dto.setTotalQuantity(dto.getTotalQuantity() + inventory.getTotalQuantity());
            dto.setLost(dto.getLost() + inventory.getLost());
            dto.setAverageUnitValue((dto.getAverageUnitValue() + inventory.getAverageUnitValue()) / 2);
            dto.setWarehouseDTO(warehouseDTO);
            sumaryMap.put(itemId, dto);

        });

        return new ArrayList<>(sumaryMap.values());
    }

    @Override
    public List<InventoryDTO> getAllInventoryForCurrentStaff() {
        // Lấy thông tin người dùng hiện tại từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // Tìm người dùng dựa trên username
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        // Kiểm tra nếu người dùng không quản lý kho nào
        if (currentUser.getWarehouse() == null) {
            throw new NotFoundException("Người dùng này không quản lý kho nào");
        }

        Long warehouseId = currentUser.getWarehouse().getId();

        // Lấy danh sách inventory dựa trên warehouseId
        List<Inventory> inventories = inventoryRepository.findAllByWarehouseId(warehouseId);

        if (inventories.isEmpty()) {
            throw new NotFoundException("Không tìm thấy tồn kho nào cho kho với ID: " + warehouseId);
        }

        return inventories.stream()
                .map(inventory ->{
                    InventoryDTO dto = modelMapper.map(inventory, InventoryDTO.class);
                    dto.setItemCode(inventory.getItem().getCode());
                    dto.setItemId(inventory.getItem().getId());
                    dto.setItemName(inventory.getItem().getSubCategory().getName());

                    return dto;
                } )
                .collect(Collectors.toList());
    }
    private Map<Long, Boolean> lowStockNotificationSentMap = new ConcurrentHashMap<>();
    @Scheduled(fixedRate = 600000)
    public void checkAndNotifyLowStock() {
        List<Item> allItems = itemRepository.findAll();
        Map<Long, Integer> totalQuantities = getTotalQuantitiesByItem();

        for (Item item : allItems) {
            int totalQuantity = totalQuantities.getOrDefault(item.getId(), 0);
            Boolean notificationSent = lowStockNotificationSentMap.getOrDefault(item.getId(), false);

            if (!notificationSent && totalQuantity < item.getMinStockLevel()) {
                sendLowStockNotification(item);
                lowStockNotificationSentMap.put(item.getId(), true);
            } else if (notificationSent && totalQuantity >= item.getMinStockLevel()) {
                // If the stock level is restored, reset the notification flag
                lowStockNotificationSentMap.put(item.getId(), false);
            }
        }
    }
    private Map<Long, Boolean> notificationSentMap = new ConcurrentHashMap<>();
    @Scheduled(fixedRate = 600000)
    public void checkAndNotifyHighStock(){
        List<Item> allItems = itemRepository.findAll();
        Map<Long, Integer> totalQuantities = getTotalQuantitiesByItem();

        for (Item item : allItems) {
            int totalQuantity = totalQuantities.getOrDefault(item.getId(), 0);
            Boolean notificationSent = notificationSentMap.getOrDefault(item.getId(), false);

            if (!notificationSent && totalQuantity > item.getMaxStockLevel()) {
                sendHighStockNotification(item);
                notificationSentMap.put(item.getId(), true);
            } else if (notificationSent && totalQuantity <= item.getMaxStockLevel()) {
                // If the stock level goes back to normal, reset the notification flag
                notificationSentMap.put(item.getId(), false);
            }
        }

    }
    //@Cacheable("totalQuantities")
    private Map<Long, Integer> getTotalQuantitiesByItem() {
        List<Inventory> inventories = inventoryRepository.findAll();
        return inventories.stream()
                .collect(Collectors.groupingBy(
                        inventory -> inventory.getItem().getId(),
                        Collectors.summingInt(Inventory::getTotalQuantity)
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
