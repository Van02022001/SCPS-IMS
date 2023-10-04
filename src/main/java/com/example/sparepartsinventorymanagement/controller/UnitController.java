package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.ProductFormRequest;
import com.example.sparepartsinventorymanagement.dto.request.UnitFormRequest;
import com.example.sparepartsinventorymanagement.dto.request.UpdateProductForm;
import com.example.sparepartsinventorymanagement.service.impl.UnitServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/units")
public class UnitController {

    @Autowired
    private UnitServiceImpl unitService;

    @Operation(summary = "For get list of units")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        return unitService.getAll();
    }

    @Operation(summary = "For search list units by name")
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUnitsByName(
            @Parameter(description = "enter keyword to search", required = true)
            @NotEmpty @NotBlank String keyword
    ) {
        return unitService.findByName(keyword);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For create unit")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> creatUnit(
            @Valid @RequestBody UnitFormRequest form
    ) {
        return unitService.createUnit(form);
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For update unit")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUnit(
            @Parameter(description = "Enter unit id", required = true, example = "1") @NotNull @PathVariable(name = "id") Long id,
            @Valid @RequestBody UnitFormRequest form
    ) {
        return unitService.updateUnit(id, form);
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For delete unit")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteUnit(
            @Parameter(description = "Enter unit id", required = true, example = "1") @NotNull @PathVariable(name = "id") Long id
    ) {
        return unitService.deleteUnit(id);
    }
}
