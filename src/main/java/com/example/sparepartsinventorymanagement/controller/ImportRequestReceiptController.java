package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.ImportRequestReceiptForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateImportRequestReceipt;
import com.example.sparepartsinventorymanagement.dto.response.ImportRequestReceiptResponse;
import com.example.sparepartsinventorymanagement.dto.response.NotificationDTO;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
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
import java.util.Map;

@Tag(name = "import-request-receipt")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/import-request-receipts")
public class ImportRequestReceiptController {
    private final ReceiptService receiptService;
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Create a new import request receipt")
    @PostMapping( consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createImportRequest(@RequestBody ImportRequestReceiptForm importRequestReceiptForm) {
        try {
            var receiptResponse = receiptService.createImportRequestReceipt(importRequestReceiptForm);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(
                    HttpStatus.CREATED.toString(),
                    "Import request receipt created successfully",
                    receiptResponse
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
                    "An error occurred while creating the import request receipt",
                    null
            ));
        }
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Get all import request receipts")
    @GetMapping( produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllImportRequestReceipts() {
        try {
            List<ImportRequestReceiptResponse> receipts = receiptService.getAllImportRequestReceipts();
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Import request receipts retrieved successfully",
                    receipts
            ));
        }
        catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    e.getMessage(),
                    null
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                    "An error occurred while retrieving the import request receipts",
                    null
            ));
        }
    }

    @PreAuthorize(" hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Get all import request receipts")
    @GetMapping("/warehouse")
    public ResponseEntity<?> getAllImportRequestReceiptsByWarehouse() {
        try {
            List<ImportRequestReceiptResponse> receipts = receiptService.getAllImportRequestReceiptsByWareHouse();
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Import request receipts retrieved successfully",
                    receipts
            ));
        }
        catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    e.getMessage(),
                    null
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                    "An error occurred while retrieving the import request receipts",
                    null
            ));
        }
    }


    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Get an import request receipt by ID")
    @GetMapping(value ="/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getImportRequestReceiptById(@PathVariable Long id) {
        try {
            ImportRequestReceiptResponse receipt = receiptService.getImportRequestReceiptById(id);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Import request receipt retrieved successfully",
                    receipt
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
                    "An error occurred while retrieving the import request receipt",
                    null
            ));
        }
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Update an import request receipt")
    @PutMapping(value = "/{id}",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateImportRequestReceipt(@PathVariable Long id, @RequestBody UpdateImportRequestReceipt importRequestReceiptForm) {
        try {
            ImportRequestReceiptResponse updatedReceipt = receiptService.updateImportRequestReceipt(id, importRequestReceiptForm);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Import request receipt updated successfully",
                    updatedReceipt
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
                    "An error occurred while updating the import request receipt",
                    null
            ));
        }
    }
    @PreAuthorize("hasRole('ROLE_MANAGER') ")
    @Operation(summary = "Delete an import request receipt")
    @DeleteMapping(value ="/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteImportRequestReceipt(@PathVariable Long id) {
        try {
            receiptService.deleteImportRequestReceipt(id);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Import request receipt deleted successfully",
                    null
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
                    "An error occurred while deleting the import request receipt",
                    null
            ));
        }
    }
    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Confirm an import request receipt")
    @PutMapping(value ="/confirm/{receiptId}",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> confirmImportRequestReceipt(@PathVariable Long receiptId) {
        try {
            receiptService.confirmImportRequestReceipt(receiptId);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Import request receipt confirmed successfully",
                    null
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
                    "An error occurred while confirming the import request receipt",
                    null
            ));
        }
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Cancel an import request receipt")
    @PutMapping(value ="/cancel/{receiptId}",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> cancelImportRequestReceipt(@PathVariable Long receiptId) {
        try {
            receiptService.cancelImportRequestReceipt(receiptId);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Import request receipt canceled successfully",
                    null
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
                    "An error occurred while canceling the import request receipt",
                    null
            ));
        }
    }
    @PutMapping("/{receiptId}/start-import")
    @Operation(summary = "Import is Processing")
    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    public ResponseEntity<?> startImportProcess(@PathVariable Long receiptId) {
        try {
            receiptService.startImportProcess(receiptId);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Import process started successfully",
                    null
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
                    "An error occurred while starting the import process",
                    null
            ));
        }
    }
}
