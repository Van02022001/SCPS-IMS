package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.CheckInventoryReceiptForm;
import com.example.sparepartsinventorymanagement.dto.request.InventoryCheckDetail;
import com.example.sparepartsinventorymanagement.dto.response.CheckInventoryReceiptResponse;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.service.ReceiptService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "inventory-check")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inventory-checks")
public class InventoryCheckController {
    private final ReceiptService receiptService;

    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Create a new inventory check receipt")
    @PostMapping()
    public ResponseEntity<?> createInventoryCheckReceipt(@RequestBody CheckInventoryReceiptForm checkDetails) {
        try {
            CheckInventoryReceiptResponse checkInventoryReceiptResponse = receiptService.createCheckInventoryReceipt(checkDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(
                    HttpStatus.CREATED.toString(),
                    "Inventory check receipt created successfully",
                    checkInventoryReceiptResponse
            ));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    e.getMessage(),
                    null
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),
                    e.getMessage(),
                    null
            ));
        } catch (Exception e) {
            // Log the exception details here
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                    "An error occurred while creating the inventory check receipt",
                    null
            ));
        }
    }
}
