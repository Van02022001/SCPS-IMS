package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.CreateBrandFrom;
import com.example.sparepartsinventorymanagement.dto.request.OriginFormRequest;
import com.example.sparepartsinventorymanagement.dto.request.UpdateBrandFrom;
import com.example.sparepartsinventorymanagement.service.impl.OriginServiceImpl;
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
@RequestMapping("/api/v1/origins")
@Tag(name = "origin")
public class OriginController {
    @Autowired
    private OriginServiceImpl originService;

    @Operation(summary = "For get list of origins")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        return originService.getAll();
    }

    @Operation(summary = "For get list of origins by name")
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getOriginByName(
            @Parameter(description = "Enter keyword to search", required = true)
            @NotEmpty @NotBlank String keyword
    ) {
        return originService.findByName(keyword);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For create origin")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createBrand(
            @Valid @RequestBody OriginFormRequest form
    ) {
        return originService.createOrigin(form);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For update origin")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateBrand(
            @Parameter(description = "Enter origin id to update", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id,
            @Valid @RequestBody OriginFormRequest form
    ) {
        return originService.updateOrigin(id, form);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For delete origin")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteOrigin(
            @Parameter(description = "Enter origin id to delete", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id
    ) {
        return originService.deleteOrigin(id);
    }
}
