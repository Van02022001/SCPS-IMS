package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.CategoryFormRequest;
import com.example.sparepartsinventorymanagement.dto.response.GetCategoryDTO;
import com.example.sparepartsinventorymanagement.entities.CategoryStatus;
import com.example.sparepartsinventorymanagement.service.impl.CategoryServiceImpl;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "category")
public class CategoryController {
    @Autowired
    private CategoryServiceImpl categoryService;
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For get list of categories")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        List<GetCategoryDTO> res = categoryService.getAll();
        if(res.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
               HttpStatus.NOT_FOUND.toString(),
               "List is empty",
               null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list categories is successfully",
                res
        ));
    }
    @Operation(summary = "For get category by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCategoryById(
            @Parameter(description = "enter category id to get", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id
    ) {
        GetCategoryDTO res = categoryService.getCategoryById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get category by id successfully",
                res
        ));
    }

    @Operation(summary = "For get category contains keyword by name")
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCategoryByName(
            @Parameter(description = "enter keyword to search", required = true)
            @NotEmpty @NotBlank String keyword
    ) {
        List<GetCategoryDTO> res = categoryService.searchCategoryByName(keyword.trim());
        if(res.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    "List is empty",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list categories by keyword successfully",
                res
        ));
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For create category")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCategory(
            @Valid @RequestBody CategoryFormRequest form
            ) {
        GetCategoryDTO res = categoryService.createCategory(form);
        if(res == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Create category failed",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Create category successfully",
                res
        ));
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For update category")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateCategory(
            @Parameter(description = "enter category id", required = true, example = "1") @NotNull @PathVariable(name = "id") Long id,
            @Valid @RequestBody CategoryFormRequest form
    ) {
        GetCategoryDTO res = categoryService.updateCategory(id, form);
        if(res == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Update failed",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Update category successfully",
                res
        ));
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
        GetCategoryDTO res = categoryService.updateCategoryStatus(id, status);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Update category status successfully",
                res
        ));
    }

    @Operation(summary = "For get list of categories by status is active")
    @GetMapping(value = "/active-categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getActiveCategories() {
        List<GetCategoryDTO> res = categoryService.getActiveCategories();
        if(res.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    "List is empty",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list categories by active status successfully",
                res
        ));
    }
}
