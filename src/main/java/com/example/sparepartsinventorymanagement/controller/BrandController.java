package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.CreateBrandFrom;
import com.example.sparepartsinventorymanagement.dto.request.UpdateBrandFrom;
import com.example.sparepartsinventorymanagement.service.impl.BrandServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/brands")
@Tag(name = "brand")
public class BrandController {

    @Autowired
    private BrandServiceImpl brandService;

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For get list of brand")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        return brandService.getAll();

    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For brand by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getBrandById(
            @Parameter(description = "Enter brand to get", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id
    ) {
        return brandService.getBrandById(id);
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For create brand")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createBrand(
            @Valid @RequestBody CreateBrandFrom form
    ) {
        return brandService.createBrand(form);
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For update brand")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateBrand(
            @Parameter(description = "Enter brand id to update", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id,
            @Valid @RequestBody UpdateBrandFrom form
    ) {
        return brandService.updateBrand(id, form);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For list brands by name")
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getBrandByName(
            @Parameter(description = "enter keyword to search", required = true)
            @NotEmpty @NotBlank String keyword
    ) {
        return brandService.getBrandByName(keyword);
    }
}
