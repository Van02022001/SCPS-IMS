package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.TransferRequest;
import com.example.sparepartsinventorymanagement.dto.request.TransferUpdateRequest;
import com.example.sparepartsinventorymanagement.dto.response.TransferResult;
import com.example.sparepartsinventorymanagement.dto.response.WarehouseTransferDTO;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.service.WarehouseTransferService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/warehouse-transfer")
@RequiredArgsConstructor
@Tag(name = "warehouse-transfer")
public class WarehouseTransferController {
    private final WarehouseTransferService warehouseTransferService;
    @Operation(summary = "Transfer multiple items")
    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    @PostMapping("/transfer")
    public ResponseEntity<?> transferItems(@RequestBody TransferRequest transferRequests) {
        try {
            TransferResult result = warehouseTransferService.transferMultipleItems(transferRequests);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Items transferred successfully",
                    result
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
                    "Error during item transfer: " + e.getMessage(),
                    null
            ));
        }
    }
//    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')") // Adjust the role as necessary
//    @Operation(summary = "Update transfers")
//    @PostMapping("/transfers/update")
//    public ResponseEntity<?> updateTransfer(@RequestBody TransferRequest updateRequest) {
//        try {
//            TransferResult result = warehouseTransferService.updateTransfer(updateRequest);
//            return ResponseEntity.ok(new ResponseObject(
//                    HttpStatus.OK.toString(),
//                    "Chuyển kho đã được cập nhật thành công",
//                    result
//            ));
//        } catch (NotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
//                    HttpStatus.NOT_FOUND.toString(),
//                    e.getMessage(),
//                    null
//            ));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject(
//                    HttpStatus.INTERNAL_SERVER_ERROR.toString(),
//                    "Lỗi trong quá trình cập nhật chuyển kho: " + e.getMessage(),
//                    null
//            ));
//        }
//    }

    @Operation(summary = "Get all warehouse transfers")
    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')") // Adjust the role as necessary
    @GetMapping("/transfers")
    public ResponseEntity<?> getAllWarehouseTransfers() {
        try {
            List<WarehouseTransferDTO> transfers = warehouseTransferService.getAllWarehouseTransfers();
            return ResponseEntity.ok(transfers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving all warehouse transfers: " + e.getMessage());
        }
    }

    @Operation(summary = "Get warehouse transfer by ID")
    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')") // Adjust the role as necessary
    @GetMapping("/transfers/{transferId}")
    public ResponseEntity<?> getWarehouseTransferById(@PathVariable Long transferId) {
        try {
            WarehouseTransferDTO transferDTO = warehouseTransferService.getWarehouseTransferById(transferId);
            return ResponseEntity.ok(transferDTO);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving warehouse transfer: " + e.getMessage());
        }
    }

}
