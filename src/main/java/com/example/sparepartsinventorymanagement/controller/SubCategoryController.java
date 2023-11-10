package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.SubCategoryFormRequest;
import com.example.sparepartsinventorymanagement.entities.SubCategoryStatus;
import com.example.sparepartsinventorymanagement.service.impl.SubCategoryServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.util.Set;

@RestController
@RequestMapping("/api/v1/sub-categories")
@Tag(name = "sub-category")
public class SubCategoryController {
    @Autowired
    private SubCategoryServiceImpl subCategoryService;

    @Operation(summary = "For get list of Sub Category")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        return subCategoryService.getAll();
    }

    @Operation(summary = "For get SubCategory by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductById(
            @Parameter(description = "enter SubCategory id to get", example = "1", required = true) @PathVariable(name = "id") @NotBlank Long id
    ) {
        return subCategoryService.getSubCategoryById(id);
    }
    @Operation(summary = "For get Sub Categories contains keyword by name")
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductByName(
            @Parameter(description = "enter keyword to search", required = true)
            @NotEmpty @NotBlank String keyword
    ) {
        return subCategoryService.findByName(keyword);
    }
    @Operation(summary = "For get SubCategory by active status")
    @GetMapping(value = "/active-sub-category", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getActiveProducts() {
        return subCategoryService.getActiveSubCategories();
    }

    @Operation(summary = "For get SubCategory by category")
    @GetMapping(value = "/sub-categories-by-categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductsByCategory(
            @Parameter(description = "Filter with category")
            @RequestParam(name = "id", required = true) Set<Long> ids){
        return subCategoryService.getSubCategoriesByCategory(ids);
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For create SubCategory")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createSubCategory(
            @Valid @RequestBody SubCategoryFormRequest form
    ) {
        return subCategoryService.createSubCategory(form);
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For update SubCategory")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateSubCategory(
            @Parameter(description = "enter SubCategory id", required = true, example = "1") @NotNull @PathVariable(name = "id") Long id,
            @Valid @RequestBody SubCategoryFormRequest form
    ) {
        return subCategoryService.updateSubCategory(id, form);
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For update status of SubCategory")
    @PutMapping(value = "/sub-category-status/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateSubCategoryStatus(
            @Parameter(description = "enter SubCategory id", required = true, example = "1")
            @NotNull @NotEmpty @PathVariable(name = "id") Long id,
            @Parameter(description = "SubCategory status (Active or Inactive)", required = true)
            @NotNull @NotEmpty @Pattern(regexp = "Active|Inactive") @RequestParam(name = "status") SubCategoryStatus status
    ) {
        return subCategoryService.updateSubCategoryStatus(id, status);
    }

}
