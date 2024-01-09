package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.response.ImportRequestReceiptResponse;
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

@Tag(name = "import-receipt")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/import-receipts")
public class ImportReceiptController {
    private final ReceiptService receiptService;

    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Create an actual import receipt based on a request receipt")
    @PostMapping("/create-import/{receiptId}")
    public ResponseEntity<?> createImportReceipt(@PathVariable Long receiptId, @RequestBody Map<Long, Integer> actualQuantities) {
        try {
            ImportRequestReceiptResponse receiptResponse = receiptService.createImportReceipt(receiptId, actualQuantities);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(
                    HttpStatus.CREATED.toString(),
                    "Actual import receipt created successfully based on request receipt #" + receiptId,
                    receiptResponse
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
                    "An error occurred while creating the actual import receipt",
                    null
            ));
        }
    }
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Get all import receipts")
    @GetMapping( produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllImportRequestReceipts() {
        try {
            List<ImportRequestReceiptResponse> receipts = receiptService.getAllImportReceipts();
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Import  receipts retrieved successfully",
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
    @Operation(summary = "Get an import receipt by ID")
    @GetMapping(value ="/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getImportRequestReceiptById(@PathVariable Long id) {
        try {
            ImportRequestReceiptResponse receipt = receiptService.getImportReceiptById(id);
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

    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF') ")
    @Operation(summary = "Delete an import receipt")
    @DeleteMapping(value ="/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteImportRequestReceipt(@PathVariable Long id) {
        try {
            receiptService.deleteImportReceipt(id);
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
    @Operation(summary = "Get all import receipts")
    @GetMapping("/warehouse")
    public ResponseEntity<?> getAllImportReceiptsByWarehouse() {
        try {
            List<ImportRequestReceiptResponse> receipts = receiptService.getAllImportReceiptsByWareHouse();
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Import receipts retrieved successfully",
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
                    "An error occurred while retrieving the import receipts",
                    null
            ));
        }
    }
}
