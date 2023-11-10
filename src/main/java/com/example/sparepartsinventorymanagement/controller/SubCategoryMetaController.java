package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.CreateProductMetaForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateProductMetaForm;
import com.example.sparepartsinventorymanagement.service.impl.SubCategoryMetaServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sub-category-metas")
@Tag(name = "sub-category-meta")
public class SubCategoryMetaController {
    @Autowired
    private SubCategoryMetaServiceImpl productMetaService;

    @Operation(summary = "For get list of sub category meta by product")
    @GetMapping(value = "/getProductMetasByProduct/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll(
            @Parameter(description = "Enter sub category id to get", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id
    ) {
        return productMetaService.getAllBySubCategory(id);
    }

    @Operation(summary = "For sub category meta by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductMetaById(
            @Parameter(description = "Enter sub category id to get", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id
    ) {
        return productMetaService.getSubCategoryMetaById(id);
    }

    @Operation(summary = "For create sub category meta")
    @PostMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createProductMeta(
            @Parameter(description = "Enter sub category id to get", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id,
            @Valid @RequestBody CreateProductMetaForm form
    ) {
        return productMetaService.createSubCategoryMeta(id, form);
    }

    @Operation(summary = "For update sub category meta")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProductMeta(
            @Parameter(description = "Enter sub category id to get", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id,
            @Valid @RequestBody UpdateProductMetaForm form
    ) {
        return productMetaService.updateSubCategoryMeta(id,form);
    }

    @Operation(summary = "For delete sub category meta")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createProductMeta(
            @Parameter(description = "Enter sub category id to get", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id
    ) {
        return productMetaService.deleteSubCategoryMeta(id);
    }
}
