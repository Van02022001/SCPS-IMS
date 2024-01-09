package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.WarehouseFormRequest;
import com.example.sparepartsinventorymanagement.dto.response.InventoryStaffDTO;
import com.example.sparepartsinventorymanagement.dto.response.WarehouseDTO;
import com.example.sparepartsinventorymanagement.service.impl.WarehouseServiceImpl;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouses")
@Tag(name = "warehouse")
public class WarehouseController {

    private final WarehouseServiceImpl warehouseService;


    @Operation(summary = "For get list of warehouses")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        List<WarehouseDTO> res = warehouseService.getAll();
        if(res.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "List is empty",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list warehouse successfully",
                res
        ));
    }

    @Operation(summary = "For get warehouse by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getWarehouseById(
            @Parameter(description = "Enter id to get", example = "1", required = true) @PathVariable(name = "id") @NotBlank Long id
    ) {
        WarehouseDTO res = warehouseService.getWarehouseById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get warehouse successfully",
                res
        ));
    }

    @Operation(summary = "For get warehouses contains keyword by name")
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getWarehousesByName(
            @Parameter(description = "Enter keyword to search", required = true)
            @NotEmpty @NotBlank String keyword
    ) {
        List<WarehouseDTO> res = warehouseService.getWarehouseByName(keyword);
        if(res.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "List is empty",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list warehouse by name successfully",
                res
        ));
    }

    @Operation(summary = "For get warehouses by active status")
    @GetMapping(value = "/active-warehouses", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getActiveWarehouses() {
        List<WarehouseDTO> res = warehouseService.getWarehousesByActiveStatus();
        if(res.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "List is empty",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list warehouse active status successfully",
                res
        ));
    }


    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For create warehouse")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createWarehouse(
            @Valid @RequestBody WarehouseFormRequest form
    ) {
        WarehouseDTO res = warehouseService.createWarehouse(form);
        if(res == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Create warehouse failed",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Create warehouse successfully",
                res
        ));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For update warehouse")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateWarehouse(
            @Parameter(description = "Enter id", required = true, example = "1") @NotNull @PathVariable(name = "id") Long id,
            @Valid @RequestBody WarehouseFormRequest form
    ) {
        WarehouseDTO res = warehouseService.updateWarehouse(id, form);
        if(res == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Update warehouse failed",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Update warehouse successfully",
                res
        ));
    }


    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('SALE_STAFF')")
    @Operation(summary = "Get all inventory staff by warehouse ID")
    @GetMapping("/inventory-staffs/{warehouseId}")
    public ResponseEntity<?> getAllInventoryStaffByWarehouseId(
            @Parameter(description = "Enter warehouse ID", required = true, example = "1")
            @PathVariable Long warehouseId) {
        try {
            List<InventoryStaffDTO> inventoryStaffDTOs = warehouseService.getAllInventoryStaffByWarehouseId(warehouseId);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Get all inventory staff by warehouse ID successfully",
                    inventoryStaffDTOs
            ));
        } catch (Exception e) {
            // Xử lý lỗi ở đây, ví dụ ghi log hoặc trả về thông báo lỗi cụ thể cho người dùng
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Failed to get inventory staff by warehouse ID",
                    null
            ));
        }
    }
    @Operation(summary = "For get warehouses except current warehouse")
    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    @GetMapping(value = "/other-warehouses", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getWarehousesExceptCurrentWarehouse() {
        List<WarehouseDTO> res = warehouseService.getWarehousesExceptCurrentWarehouse();
        if(res.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Danh sách rỗng",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Lấy danh sách kho trừ kho hiện tại thành công.",
                res
        ));
    }

//    @PreAuthorize("hasRole('ROLE_MANAGER')")
//    @Operation(summary = "For update status of warehouse")
//    @PutMapping(value = "/warehouse-status/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> updateWarehouseStatus(
//            @Parameter(description = "Enter warehouse id", required = true, example = "1")
//            @NotNull @NotEmpty @PathVariable(name = "id") Long id,
//            @Parameter(description = "Warehouse status (Active or Inactive)", required = true)
//            @NotNull @NotEmpty @Pattern(regexp = "Active|Inactive") @RequestParam(name = "status") WarehouseStatus status
//    ) {
//        return warehouseService.updateWarehouseStatus(id, status);
//    }
}
