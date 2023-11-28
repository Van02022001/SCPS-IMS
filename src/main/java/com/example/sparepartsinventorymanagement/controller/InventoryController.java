package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.response.InventoryDTO;
import com.example.sparepartsinventorymanagement.dto.response.InventoryItemSummaryDTO;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.service.InventoryService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "inventory")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/inventories")
public class InventoryController {
    private final InventoryService inventoryService;
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Get all inventories for a specific warehouse")
    @GetMapping(value="/warehouse/{warehouseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllInventoriesByWarehouse(@PathVariable Long warehouseId) {
        try {
            List<InventoryDTO> inventories = inventoryService.getAllInventoryByWarehouse(warehouseId);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Inventories retrieved successfully for warehouse with ID " + warehouseId,
                    inventories
            ));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    e.getMessage(),
                    null
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                    "An error occurred while retrieving inventories",
                    null
            ));
        }
    }
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Get consolidated inventory summary by item")
    @GetMapping(value="/summary-by-item", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getConsolidatedInventoryByItem() {
        try {
            List<InventoryDTO> inventorySummary = inventoryService.getConsolidatedInventoryByItem();
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Consolidated inventory summary retrieved successfully",
                    inventorySummary
            ));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    e.getMessage(),
                    null
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                    "An error occurred while retrieving the consolidated inventory summary",
                    null
            ));
        }
    }

    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Get all inventories for the warehouse managed by the current staff")
    @GetMapping(value="/current-warehouse", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllInventoriesForCurrentStaff() {
        try {
            List<InventoryDTO> inventories = inventoryService.getAllInventoryForCurrentStaff();
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Inventories retrieved successfully for the current staff's warehouse",
                    inventories
            ));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    e.getMessage(),
                    null
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                    "An error occurred while retrieving inventories",
                    null
            ));
        }
    }
}
