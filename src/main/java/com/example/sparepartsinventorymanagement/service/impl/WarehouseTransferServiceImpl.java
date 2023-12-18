package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.ItemTransferDetail;
import com.example.sparepartsinventorymanagement.dto.request.TransferRequest;
import com.example.sparepartsinventorymanagement.dto.request.TransferUpdateRequest;
import com.example.sparepartsinventorymanagement.dto.response.TransferResult;
import com.example.sparepartsinventorymanagement.dto.response.WarehouseTransferDTO;
import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.jwt.userprincipal.Principal;
import com.example.sparepartsinventorymanagement.repository.*;
import com.example.sparepartsinventorymanagement.service.WarehouseTransferService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseTransferServiceImpl implements WarehouseTransferService {


    private final ItemRepository itemRepository;


    private final WarehouseRepository warehouseRepository;


    private final InventoryRepository inventoryRepository;

private final UserRepository userRepository;
    private final WarehouseTransferRepository warehouseTransferRepository;
    @Transactional
    @Override
    public TransferResult transferMultipleItems(TransferRequest request) {
        Warehouse destinationWarehouse = warehouseRepository.findById(request.getDestinationWarehouseId())
                .orElseThrow(() -> new NotFoundException("Destination warehouse not found"));

        User currentUser = getCurrentAuthenticatedUser();
        Warehouse sourceWarehouse = currentUser.getWarehouse(); // Giả sử mỗi InventoryStaff quản lý một kho

        for (ItemTransferDetail detail : request.getItems()) {
            transferItem(detail.getItemId(), sourceWarehouse.getId(),
                    destinationWarehouse.getId(), detail.getQuantity());
        }
        return new TransferResult(
                sourceWarehouse.getId(),
                destinationWarehouse.getId(),
                request.getItems()
        );
    }




    @Override
    public List<WarehouseTransferDTO> getAllWarehouseTransfers() {
        return warehouseTransferRepository.findAll().stream()
                .sorted(Comparator.comparing(WarehouseTransfer::getCreationDate))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public WarehouseTransferDTO getWarehouseTransferById(Long transferId) {
        WarehouseTransfer transfer = warehouseTransferRepository.findById(transferId)
                .orElseThrow(() -> new NotFoundException("WarehouseTransfer not found with id: " + transferId));
        return convertToDTO(transfer);
    }


    private void reverseTransfer(WarehouseTransfer transfer) {
        // Reverse the transfer from the destination back to the source
        Inventory sourceInventory = inventoryRepository.findByItemAndWarehouse(transfer.getItem(), transfer.getSourceWarehouse())
                .orElseThrow(() -> new RuntimeException("Source inventory record not found"));
        Inventory destinationInventory = inventoryRepository.findByItemAndWarehouse(transfer.getItem(), transfer.getDestinationWarehouse())
                .orElseThrow(() -> new RuntimeException("Destination inventory record not found"));

        double unitValue = getCurrentUnitValue(transfer.getItem());
        double totalValueTransferred = unitValue * transfer.getQuantity();

        // Update the source inventory, adding back the transferred quantity and value
        sourceInventory.setAvailable(sourceInventory.getAvailable() + transfer.getQuantity());
        sourceInventory.setOutboundQuantity(sourceInventory.getOutboundQuantity() - transfer.getQuantity());
        sourceInventory.setTotalQuantity(sourceInventory.getTotalQuantity() + transfer.getQuantity());
        sourceInventory.setTotalValue(sourceInventory.getTotalValue() + totalValueTransferred);
        inventoryRepository.save(sourceInventory);

        // Update the destination inventory, subtracting the transferred quantity and value
        destinationInventory.setAvailable(destinationInventory.getAvailable() - transfer.getQuantity());
        destinationInventory.setInboundQuantity(destinationInventory.getInboundQuantity() - transfer.getQuantity());
        destinationInventory.setTotalQuantity(destinationInventory.getTotalQuantity() - transfer.getQuantity());
        destinationInventory.setTotalValue(destinationInventory.getTotalValue() - totalValueTransferred);
        inventoryRepository.save(destinationInventory);


    }

    private void transferItem(Long itemId, Long sourceWarehouseId, Long destinationWarehouseId, int quantity) {
        // Validate the existence of item and warehouses
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        Warehouse sourceWarehouse = warehouseRepository.findById(sourceWarehouseId)
                .orElseThrow(() -> new RuntimeException("Source warehouse not found"));
        Warehouse destinationWarehouse = warehouseRepository.findById(destinationWarehouseId)
                .orElseThrow(() -> new RuntimeException("Destination warehouse not found"));
        // Assuming you have a method to get the current price of an item
        double unitValue = getCurrentUnitValue(item);

        // Calculate the total value being transferred
        double totalValueTransferred = unitValue * quantity;


        // Check if the item exists in the destination warehouse
        Inventory destinationInventory = inventoryRepository.findByItemAndWarehouse(item, destinationWarehouse)
                .orElse(null);

        // If the item doesn't exist in the destination warehouse, create a new entry
        if (destinationInventory == null) {
            destinationInventory = new Inventory();
            destinationInventory.setItem(item);
            destinationInventory.setWarehouse(destinationWarehouse);
            destinationInventory.setAvailable(quantity);
            destinationInventory.setInboundQuantity(quantity);
            destinationInventory.setInboundValue(totalValueTransferred);
            destinationInventory.setOutboundQuantity(0);
            destinationInventory.setTotalQuantity(quantity);
            destinationInventory.setTotalValue(getCurrentUnitValue(item) * quantity);
        } else {
            // Update the existing entry
            destinationInventory.setAvailable(destinationInventory.getAvailable() + quantity);
            destinationInventory.setInboundQuantity(destinationInventory.getInboundQuantity() + quantity);
            destinationInventory.setInboundValue(destinationInventory.getInboundValue() + totalValueTransferred);
            destinationInventory.setTotalQuantity(destinationInventory.getTotalQuantity() + quantity);
            destinationInventory.setTotalValue(destinationInventory.getTotalValue() + getCurrentUnitValue(item) * quantity);
        }

        // Save the destination inventory
        inventoryRepository.save(destinationInventory);



        // Fetch inventories and update
        Inventory sourceInventory = inventoryRepository.findByItemAndWarehouse(item, sourceWarehouse)
                .orElseThrow(() -> new RuntimeException("Item inventory not found in source warehouse"));
        sourceInventory.setAvailable(sourceInventory.getAvailable() - quantity);
        sourceInventory.setOutboundQuantity(sourceInventory.getOutboundQuantity() + quantity);
        sourceInventory.setOutboundValue(sourceInventory.getOutboundValue() + totalValueTransferred);
        sourceInventory.setTotalQuantity(sourceInventory.getTotalQuantity() - quantity );
        sourceInventory.setTotalValue(sourceInventory.getTotalValue() - totalValueTransferred);
        inventoryRepository.save(sourceInventory);

//        Inventory destinationInventory = inventoryRepository.findByItemAndWarehouse(item, destinationWarehouse)
//                .orElseThrow(() -> new RuntimeException("Item inventory not found in destination warehouse"));
//        destinationInventory.setAvailable(destinationInventory.getAvailable() + quantity);
//        destinationInventory.setInboundQuantity(destinationInventory.getInboundQuantity() + quantity);
//        destinationInventory.setTotalQuantity(destinationInventory.getTotalQuantity()+quantity);
//        destinationInventory.setTotalValue(destinationInventory.getTotalValue() + totalValueTransferred);
//        inventoryRepository.save(destinationInventory);

        // Record the transfer
        WarehouseTransfer transfer = new WarehouseTransfer();
        transfer.setItem(item);
        transfer.setSourceWarehouse(sourceWarehouse);
        transfer.setDestinationWarehouse(destinationWarehouse);
        transfer.setQuantity(quantity);
        transfer.setTransferDate(new Date());
        transfer.setCreatedBy(getCurrentAuthenticatedUser());
        transfer.setLastModifiedBy(getCurrentAuthenticatedUser());
        transfer.setCreationDate(new Date());
        transfer.setLastModifiedDate(new Date());
        warehouseTransferRepository.save(transfer);
    }




    private double getCurrentUnitValue(Item item) {
        // This method should return the current unit value of the item.
        // It might involve fetching the latest PurchasePrice record for the item.
        // Here's a placeholder return statement.
        return item.getPurchasePrice().getPrice(); // Ensure this method or property exists in your Item entity.
    }

    private User getCurrentAuthenticatedUser() {
        // Logic to get the current authenticated user
        return userRepository.findById(((Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private WarehouseTransferDTO convertToDTO(WarehouseTransfer transfer) {
        WarehouseTransferDTO dto = new WarehouseTransferDTO();
        dto.setId(transfer.getId());
        dto.setQuantity(transfer.getQuantity());
        dto.setTransferDate(transfer.getTransferDate());
        dto.setDestinationWarehouseId(transfer.getDestinationWarehouse().getId());
        dto.setSourceWarehouseId(transfer.getSourceWarehouse().getId());
        dto.setItemId(transfer.getItem().getId());
        dto.setCreationDate(transfer.getCreationDate());
        dto.setLastModifiedDate(transfer.getLastModifiedDate());
        dto.setCreatedByUserId(transfer.getCreatedBy() != null ? transfer.getCreatedBy().getId() : null);
        dto.setLastModifiedByUserId(transfer.getLastModifiedBy() != null ? transfer.getLastModifiedBy().getId() : null);
        return dto;
    }

}
