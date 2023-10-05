package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.CreateCategoryForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateCategoryForm;
import com.example.sparepartsinventorymanagement.entities.CategoryStatus;
import com.example.sparepartsinventorymanagement.service.impl.CategoryServiceImpl;
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
@RequestMapping("/api/v1/categories")
public class CategoryController {
    @Autowired
    private CategoryServiceImpl categoryService;
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For get list of categories")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        return categoryService.getAll();
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For get category by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCategoryById(
            @Parameter(description = "enter category id to get", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id
    ) {
        return categoryService.getCategoryById(id);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For get category contains keyword by name")
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCategoryByName(
            @Parameter(description = "enter keyword to search", required = true)
            @NotEmpty @NotBlank String keyword
    ) {
        return categoryService.searchCategoryByName(keyword);
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For create category")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCategory(
            @Valid @RequestBody CreateCategoryForm form
            ) {
        return categoryService.createCategory(form);
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For update category")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateCategory(
            @Parameter(description = "enter category id", required = true, example = "1") @NotNull @PathVariable(name = "id") Long id,
            @Valid @RequestBody UpdateCategoryForm form
    ) {
        return categoryService.updateCategory(id, form);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For update status of category")
    @PutMapping(value = "/category-status/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateCategoryStatus(
            @Parameter(description = "enter category id", required = true, example = "1")
            @NotNull @NotEmpty @PathVariable(name = "id") Long id,
            @Parameter(description = "Category status (Active or Inactive)", required = true)
            @NotNull @NotEmpty @Pattern(regexp = "Active|Inactive") @RequestParam(name = "status") CategoryStatus status
    ) {
        return categoryService.updateCategoryStatus(id, status);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For get list of categories by status is active")
    @GetMapping(value = "/active-categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getActiveCategories() {
        return categoryService.getActiveCategories();
    }
}
