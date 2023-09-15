package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.CreateProductMetaForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateProductMetaForm;
import com.example.sparepartsinventorymanagement.service.impl.ProductMetaServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/productMetas")
public class ProductMetaController {
    @Autowired
    private ProductMetaServiceImpl productMetaService;

    @Operation(summary = "For get list of product meta by product")
    @GetMapping(value = "/getProductMetasByProduct/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll(
            @Parameter(description = "Enter product id to get", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id
    ) {
        return productMetaService.getAllByProduct(id);
    }

    @Operation(summary = "For product meta by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductMetaById(
            @Parameter(description = "Enter product id to get", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id
    ) {
        return productMetaService.getProductMetaById(id);
    }

    @Operation(summary = "For create product meta")
    @PostMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createProductMeta(
            @Parameter(description = "Enter product id to get", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id,
            @Valid @RequestBody CreateProductMetaForm form
    ) {
        return productMetaService.createProductMeta(id, form);
    }

    @Operation(summary = "For update product meta")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProductMeta(
            @Parameter(description = "Enter product id to get", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id,
            @Valid @RequestBody UpdateProductMetaForm form
    ) {
        return productMetaService.updateProductMeta(id,form);
    }

    @Operation(summary = "For delete product meta")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createProductMeta(
            @Parameter(description = "Enter product id to get", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id
    ) {
        return productMetaService.deleteProductMeta(id);
    }
}
