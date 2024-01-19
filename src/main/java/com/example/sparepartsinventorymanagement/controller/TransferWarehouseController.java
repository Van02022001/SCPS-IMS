package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.ImportRequestReceiptForm;
import com.example.sparepartsinventorymanagement.dto.response.ExportReceiptResponse;
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

@RestController
@RequestMapping(value = "api/v1/transfer-warehouses")
@RequiredArgsConstructor
@Tag(name = "transfer-warehouse")
public class TransferWarehouseController {
    private final ReceiptService receiptService;

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Create a new internal import request receipt")
    @PostMapping(value ="/internal-import-request", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createInternalImportRequest(@RequestBody ImportRequestReceiptForm importRequestReceiptForm) {
        try {
            var receiptResponse = receiptService.createInternalRequestReceipt(importRequestReceiptForm);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(
                    HttpStatus.CREATED.toString(),
                    "Internal Import request receipt created successfully",
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
                    "An error occurred while creating the internal import request receipt",
                    null
            ));
        }
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Create a new internal export request receipt")
    @PostMapping(value ="/internal-export-request", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createInternalExportRequest(@RequestBody ImportRequestReceiptForm importRequestReceiptForm) {
        try {
            var receiptResponse = receiptService.createInternalExportRequestReceipt(importRequestReceiptForm);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(
                    HttpStatus.CREATED.toString(),
                    "Internal export request receipt created successfully",
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
                    "An error occurred while creating the internal export request receipt",
                    null
            ));
        }
    }
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Get all internal import receipts")
    @GetMapping( value = "/internal-import",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllInternalImportReceipts() {
        try {
            List<ImportRequestReceiptResponse> receipts = receiptService.getAllInternalImportReceipts();
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Internal Import  receipts retrieved successfully",
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
                    "An error occurred while retrieving the internal import request receipts",
                    null
            ));
        }
    }
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Get all internal export receipts for manager")
    @GetMapping( value = "/internal-export",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllInternalExportReceipts() {
        try {
            List<ImportRequestReceiptResponse> receipts = receiptService.getAllInternalExportReceipts();
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Internal export  receipts retrieved successfully",
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
                    "An error occurred while retrieving the internal export request receipts",
                    null
            ));
        }
    }
    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Create a new internal export receipt")
    @PostMapping(value = "/internal-export/{receiptId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createExportReceipt(@PathVariable Long receiptId, @RequestBody Map<Long, Integer> actualQuantities) {
        try {
            ImportRequestReceiptResponse exportReceiptResponse = receiptService.createInternalExportReceipt(receiptId, actualQuantities);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(
                    HttpStatus.CREATED.toString(),
                    "Internal Export receipt created successfully",
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
                    "An error occurred while creating the internal export receipt",
                    null
            ));
        }
    }
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Get an internal import receipt by ID")
    @GetMapping(value ="/internal-import/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getInternalImportReceiptById(@PathVariable Long id) {
        try {
            ImportRequestReceiptResponse receipt = receiptService.getInternalImportReceiptById(id);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Internal Import request receipt retrieved successfully",
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
                    "An error occurred while retrieving the internal import request receipt",
                    null
            ));
        }
    }
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Get an internal export receipt by ID")
    @GetMapping(value ="/internal-export/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getInternalExportReceiptById(@PathVariable Long id) {
        try {
            ImportRequestReceiptResponse receipt = receiptService.getInternalExportReceiptById(id);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Internal export request receipt retrieved successfully",
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
                    "An error occurred while retrieving the internal export request receipt",
                    null
            ));
        }
    }
    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Get all import receipts by warehouse for inventory staff")
    @GetMapping("/internal-import/warehouse")
    public ResponseEntity<?> getAllInternalImportReceiptsByWarehouse() {
        try {
            List<ImportRequestReceiptResponse> receipts = receiptService.getAllInternalImportReceiptsByWareHouse();
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Internal Import receipts retrieved successfully",
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
                    "An error occurred while retrieving the internal import receipts",
                    null
            ));
        }
    }
    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Get all export receipts by warehouse for inventory staff")
    @GetMapping("/internal-export/warehouse")
    public ResponseEntity<?> getAllInternalExportReceiptsByWarehouse() {
        try {
            List<ImportRequestReceiptResponse> receipts = receiptService.getAllInternalExportReceiptsByWareHouse();
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Internal Import receipts retrieved successfully",
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
                    "An error occurred while retrieving the internal export receipts",
                    null
            ));
        }
    }
    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Confirm an internal import request receipt")
    @PutMapping(value ="/internal-import-request/confirm/{receiptId}",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> confirmInternalImportRequestReceipt(@PathVariable Long receiptId) {
        try {
            receiptService.confirmInternalImportRequestReceipt(receiptId);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Internal Import request receipt confirmed successfully",
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
                    "An error occurred while confirming the internal import request receipt",
                    null
            ));
        }
    }

    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Confirm an internal export request receipt")
    @PutMapping(value ="/internal-export-request/confirm/{receiptId}",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> confirmInternalExportRequestReceipt(@PathVariable Long receiptId) {
        try {
            receiptService.confirmInternalExportRequestReceipt(receiptId);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Internal export request receipt confirmed successfully",
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
                    "An error occurred while confirming the internal export request receipt",
                    null
            ));
        }
    }



    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Create an internal import receipt based on a internal import request receipt")
    @PostMapping("/internal-import/{receiptId}")
    public ResponseEntity<?> createImportReceipt(@PathVariable Long receiptId, @RequestBody Map<Long, Integer> actualQuantities) {
        try {
            ImportRequestReceiptResponse receiptResponse = receiptService.createInternalImportReceipt(receiptId, actualQuantities);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(
                    HttpStatus.CREATED.toString(),
                    "Actual import receipt created successfully based on internal request receipt #" + receiptId,
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
                    "An error occurred while creating the actual internal import receipt",
                    null
            ));
        }
    }


    @PutMapping("/internal-import/processing/{receiptId}")
    @Operation(summary = "Import is Processing")
    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    public ResponseEntity<?> startInternalImportProcess(@PathVariable Long receiptId) {
        try {
            receiptService.startInternalImportProcess(receiptId);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Internal Import process started successfully",
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
                    "An error occurred while starting the internal import process",
                    null
            ));
        }
    }

    @PutMapping("/internal-export/processing/{receiptId}")
    @Operation(summary = "Intenal Export is Processing")
    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    public ResponseEntity<?> startInternalExportProcess(@PathVariable Long receiptId) {
        try {
            receiptService.startInternalExportProcess(receiptId);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Internal Export process started successfully",
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
                    "An error occurred while starting the internal Export process",
                    null
            ));
        }
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Get all internal import request receipts")
    @GetMapping( value="/internal-import-request",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllInternalImportRequestReceipts() {
        try {
            List<ImportRequestReceiptResponse> receipts = receiptService.getAllInternalImportRequestReceipts();
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Internal Import request receipts retrieved successfully",
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
                    "An error occurred while retrieving the internal import request receipts",
                    null
            ));
        }
    }
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Get all internal export request receipts")
    @GetMapping( value="/internal-export-request",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllInternalExportRequestReceipts() {
        try {
            List<ImportRequestReceiptResponse> receipts = receiptService.getAllInternalExportRequestReceipts();
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Internal export request receipts retrieved successfully",
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
                    "An error occurred while retrieving the internal export request receipts",
                    null
            ));
        }
    }


    @PreAuthorize(" hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Get all import request receipts")
    @GetMapping("/internal-import-request/warehouse")
    public ResponseEntity<?> getAllInternalImportRequestReceiptsByWarehouse() {
        try {
            List<ImportRequestReceiptResponse> receipts = receiptService.getAllInternalImportRequestReceiptsByWareHouse();
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Internal Import request receipts retrieved successfully",
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
                    "An error occurred while retrieving the internal import request receipts",
                    null
            ));
        }
    }
    @PreAuthorize(" hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Get all export request receipts for inventory staff")
    @GetMapping("/internal-export-request/warehouse")
    public ResponseEntity<?> getAllInternalExportRequestReceiptsByWarehouse() {
        try {
            List<ImportRequestReceiptResponse> receipts = receiptService.getAllInternalExportRequestReceiptsByWareHouse();
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Internal export request receipts retrieved successfully",
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
                    "An error occurred while retrieving the internal export request receipts",
                    null
            ));
        }
    }
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Get an internal import request receipt by ID")
    @GetMapping(value ="/internal-import-request/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getInternalImportRequestReceiptById(@PathVariable Long id) {
        try {
            ImportRequestReceiptResponse receipt = receiptService.getInternalImportRequestReceiptById(id);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Internal Import request receipt retrieved successfully",
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
                    "An error occurred while retrieving the internal import request receipt",
                    null
            ));
        }
    }
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Get an internal export request receipt by ID")
    @GetMapping(value ="/internal-export-request/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getInternalExportRequestReceiptById(@PathVariable Long id) {
        try {
            ImportRequestReceiptResponse receipt = receiptService.getInternalExportRequestReceiptById(id);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Internal Export request receipt retrieved successfully",
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
                    "An error occurred while retrieving the internal Export request receipt",
                    null
            ));
        }
    }

}
