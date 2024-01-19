package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.CheckInventoryReceiptForm;
import com.example.sparepartsinventorymanagement.dto.request.InventoryCheckDetail;
import com.example.sparepartsinventorymanagement.dto.response.CheckInventoryReceiptResponse;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.exception.QuantityExceedsInventoryException;
import com.example.sparepartsinventorymanagement.service.ReceiptService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
        } catch (QuantityExceedsInventoryException e) {
            // Handle the specific case where the requested quantity exceeds inventory availability
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

    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF') or hasRole('ROLE_MANAGER') ")
    @Operation(summary = "Get all inventory check receipts")
    @GetMapping("/receipts")
    public ResponseEntity<?> getAllInventoryCheckReceipts() {
        try {
            List<CheckInventoryReceiptResponse> checkInventoryReceiptResponses = receiptService.getAllCheckInventoryReceipts();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "All inventory check receipts retrieved successfully",
                    checkInventoryReceiptResponses
            ));
        }
        catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    e.getMessage(),
                    null
            ));
        }catch (Exception e) {
            // Log the exception details here
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                    "An error occurred while retrieving inventory check receipts",
                    null
            ));
        }
    }

    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF') or hasRole('ROLE_MANAGER')")
    @Operation(summary = "Get an inventory check receipt by ID")
    @GetMapping("/receipts/{receiptId}")
    public ResponseEntity<?> getInventoryCheckReceiptById(@PathVariable Long receiptId) {
        try {
            CheckInventoryReceiptResponse checkInventoryReceiptResponse = receiptService.getCheckInventoryReceiptById(receiptId);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Inventory check receipt retrieved successfully",
                    checkInventoryReceiptResponse
            ));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    e.getMessage(),
                    null
            ));
        } catch (Exception e) {
            // Log the exception details here
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                    "An error occurred while retrieving the inventory check receipt",
                    null
            ));
        }
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
        @Operation(summary = "Confirm an checking inventory receipt")
    @PutMapping(value ="/confirm/{receiptId}",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> confirmCheckingInventoryReceipt(@PathVariable Long receiptId) {
        try {
            receiptService.confirmCheckingInventoryReceipt(receiptId);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Checking Inventory receipt confirmed successfully",
                    null
            ));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    e.getMessage(),
                    null
            ));
        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                    "An error occurred while confirming the Checking Inventory receipt",
                    null
            ));
        }
    }
}
