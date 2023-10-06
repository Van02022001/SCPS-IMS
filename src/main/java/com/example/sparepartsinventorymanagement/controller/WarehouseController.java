package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.ProductFormRequest;
import com.example.sparepartsinventorymanagement.dto.request.WarehouseFormRequest;
import com.example.sparepartsinventorymanagement.entities.ProductStatus;
import com.example.sparepartsinventorymanagement.entities.WarehouseStatus;
import com.example.sparepartsinventorymanagement.service.impl.WarehouseServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouses")
public class WarehouseController {
    @Autowired
    private WarehouseServiceImpl warehouseService;

    @Operation(summary = "For get list of warehouses")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        return warehouseService.getAll();
    }

    @Operation(summary = "For get warehouse by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCategoryById(
            @Parameter(description = "Enter id to get", example = "1", required = true) @PathVariable(name = "id") @NotBlank Long id
    ) {
        return warehouseService.getWarehouseById(id);
    }

    @Operation(summary = "For get warehouses contains keyword by name")
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCategoryByName(
            @Parameter(description = "Enter keyword to search", required = true)
            @NotEmpty @NotBlank String keyword
    ) {
        return warehouseService.getWarehouseByName(keyword);
    }

    @Operation(summary = "For get warehouses by active status")
    @GetMapping(value = "/active-warehouses", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getActiveProducts() {
        return warehouseService.getWarehousesByActiveStatus();
    }


    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For create warehouse")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCategory(
            @Valid @RequestBody WarehouseFormRequest form
    ) {
        return warehouseService.createWarehouse(form);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For update warehouse")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateCategory(
            @Parameter(description = "Enter id", required = true, example = "1") @NotNull @PathVariable(name = "id") Long id,
            @Valid @RequestBody WarehouseFormRequest form
    ) {
        return warehouseService.updateWarehouse(id, form);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For update status of warehouse")
    @PutMapping(value = "/warehouse-status/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateCategoryStatus(
            @Parameter(description = "Enter warehouse id", required = true, example = "1")
            @NotNull @NotEmpty @PathVariable(name = "id") Long id,
            @Parameter(description = "Warehouse status (Active or Inactive)", required = true)
            @NotNull @NotEmpty @Pattern(regexp = "Active|Inactive") @RequestParam(name = "status") WarehouseStatus status
    ) {
        return warehouseService.updateWarehouseStatus(id, status);
    }
}
