package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.CustomerRequestReceiptForm;
import com.example.sparepartsinventorymanagement.dto.request.ImportRequestReceiptForm;
import com.example.sparepartsinventorymanagement.dto.response.CustomerRequestReceiptDTO;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.exception.QuantityExceedsInventoryException;
import com.example.sparepartsinventorymanagement.service.CustomerRequestReceiptService;
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

@Tag(name = "customer-request-receipt")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/customer-request-receipts")
public class CustomerRequestReceiptController {
    private final CustomerRequestReceiptService customerRequestReceiptService;

    @PreAuthorize("hasRole('ROLE_SALE_STAFF')")
    @Operation(summary = "Create a new import request receipt")
    @PostMapping( consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createImportRequest(@RequestBody CustomerRequestReceiptForm customerRequestReceiptForm) {
        try {
            var receiptResponse = customerRequestReceiptService.createCustomerRequestReceipt(customerRequestReceiptForm);
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
        } catch (QuantityExceedsInventoryException e) {
            // Handle the specific case where the requested quantity exceeds inventory availability
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),
                    e.getMessage(),
                    null
            ));
        }  catch (Exception e) {
            // Log the exception details here
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                    "An error occurred while creating the customer request receipt",
                    null
            ));
        }
    }

    @PreAuthorize("hasRole('ROLE_SALE_STAFF') or hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Get a customer request receipt by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerReceiptById(@PathVariable Long id) {
        try {
            CustomerRequestReceiptDTO receiptResponse = customerRequestReceiptService.getCustomerReceiptById(id);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Customer request receipt retrieved successfully",
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
                    "An error occurred while retrieving the customer request receipt",
                    null
            ));
        }
    }
    @PutMapping("/{customerRequestReceiptId}/start-export")
    @Operation(summary = "Import is Processing")
    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    public ResponseEntity<?> startImportProcess(@PathVariable Long customerRequestReceiptId) {
        try {
            customerRequestReceiptService.startCustomerRequestProcess(customerRequestReceiptId);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Export process started successfully",
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
                    "An error occurred while starting the export process",
                    null
            ));
        }
    }
    @PreAuthorize("hasRole('ROLE_SALE_STAFF')")
    @Operation(summary = "Cancel an customer request receipt")
    @PutMapping(value ="/cancel/{receiptId}",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> cancelImportRequestReceipt(@PathVariable Long receiptId) {
        try {
            customerRequestReceiptService.cancelCustomerRequestReceipt(receiptId);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Customer request receipt canceled successfully",
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
                    "An error occurred while canceling the Customer request receipt",
                    null
            ));
        }
    }

    @PreAuthorize("hasRole('ROLE_SALE_STAFF') or hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Get all customer request receipts")
    @GetMapping("/all")
    public ResponseEntity<?> getAllCustomerRequestReceipts() {
        try {
            List<CustomerRequestReceiptDTO> receipts = customerRequestReceiptService.getAllCustomerRequestReceipts();
            if (receipts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                        HttpStatus.NOT_FOUND.toString(), "No customer request receipts found", null
                ));
            }
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(), "Customer request receipts retrieved successfully", receipts
            ));
        } catch (Exception e) {
            // Log the exception details here
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(), "An error occurred while retrieving customer request receipts", null
            ));
        }
    }

    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Get all customer request receipts by warehousee")
    @GetMapping("/warehouse")
    public ResponseEntity<?> getAllCustomerRequestReceiptsByWarehouse() {
        try {
            List<CustomerRequestReceiptDTO> receipts = customerRequestReceiptService.getAllCustomerRequestReceiptsByWarehouse();
            if (receipts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                        HttpStatus.NOT_FOUND.toString(), "No customer request receipts found", null
                ));
            }
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(), "Customer request receipts retrieved successfully", receipts
            ));
        } catch (Exception e) {
            // Log the exception details here
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(), "An error occurred while retrieving customer request receipts", null
            ));
        }
    }

}
