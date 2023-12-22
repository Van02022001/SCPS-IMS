package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.response.ExportReceiptResponse;
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

@Tag(name = "export-receipt")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/export-receipts")
public class ExportReceiptController {
    private final ReceiptService receiptService;

    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Create a new export receipt")
    @PostMapping(value = "/{receiptId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createExportReceipt(@PathVariable Long receiptId, @RequestBody Map<Long, Integer> actualQuantities) {
        try {
            ExportReceiptResponse exportReceiptResponse = receiptService.createExportReceipt(receiptId, actualQuantities);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(
                    HttpStatus.CREATED.toString(),
                    "Export receipt created successfully",
                    exportReceiptResponse
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
                    "An error occurred while creating the export receipt",
                    null
            ));
        }
    }

    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF') or hasRole('ROLE_MANAGER')")
    @Operation(summary = "Get all export receipts")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllExportReceipts() {
        try {
            List<ExportReceiptResponse> exportReceiptResponses = receiptService.getAllExportReceipts();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "All export receipts retrieved successfully",
                    exportReceiptResponses
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
                    "An error occurred while retrieving export receipts",
                    null
            ));
        }
    }
    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF') ")
    @Operation(summary = "Get all export receipts by warehouse")
    @GetMapping("/warehouse")
    public ResponseEntity<?> getAllExportReceiptsByWarehouse() {
        try {
            List<ExportReceiptResponse> exportReceiptResponses = receiptService.getAllExportReceiptsByWareHouse();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "All export receipts retrieved successfully",
                    exportReceiptResponses
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
                    "An error occurred while retrieving export receipts",
                    null
            ));
        }
    }

    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF') or hasRole('ROLE_MANAGER')")
    @Operation(summary = "Get an export receipt by ID")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getExportReceiptById(@PathVariable Long id) {
        try {
            ExportReceiptResponse exportReceiptResponse = receiptService.getExportReceiptById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Export receipt retrieved successfully",
                    exportReceiptResponse
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
                    "An error occurred while retrieving the export receipt",
                    null
            ));
        }
    }
    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')  ")
    @Operation(summary = "Delete an export receipt by ID")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteExportReceiptById(@PathVariable Long id) {
        try {
            receiptService.deleteExportReceipt(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ResponseObject(
                    HttpStatus.NO_CONTENT.toString(),
                    "Export receipt deleted successfully",
                    null
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
                    "An error occurred while deleting the export receipt",
                    null
            ));
        }
    }




}
