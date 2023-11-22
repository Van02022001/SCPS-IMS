package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.SubCategoryFormRequest;
import com.example.sparepartsinventorymanagement.dto.response.SubCategoryDTO;
import com.example.sparepartsinventorymanagement.entities.SubCategoryStatus;
import com.example.sparepartsinventorymanagement.service.impl.SubCategoryServiceImpl;
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
        List<SubCategoryDTO> res = subCategoryService.getAll();
        if(res.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    "List empty.",
                    res
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
           HttpStatus.OK.toString(),
           "Get list of sub category successfully.",
           res
        ));
    }

    @Operation(summary = "For get SubCategory by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductById(
            @Parameter(description = "enter SubCategory id to get", example = "1", required = true) @PathVariable(name = "id") @NotBlank Long id
    ) {
        SubCategoryDTO res = subCategoryService.getSubCategoryById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list of sub category successfully.",
                res
        ));
    }
    @Operation(summary = "For get Sub Categories contains keyword by name")
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductByName(
            @Parameter(description = "Enter keyword to search", required = true)
            @NotEmpty @NotBlank String keyword
    ) {
        List<SubCategoryDTO> res = subCategoryService.findByName(keyword);
        if(res.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    "List empty.",
                    res
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list of sub category by name successfully.",
                res
        ));
    }
    @Operation(summary = "For get SubCategory by active status")
    @GetMapping(value = "/active-sub-category", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getActiveProducts() {
        List<SubCategoryDTO> res = subCategoryService.getActiveSubCategories();
        if(res.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    "List empty.",
                    res
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list of sub category by active status successfully.",
                res
        ));
    }

    @Operation(summary = "For get SubCategory by category")
    @GetMapping(value = "/sub-categories-by-categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductsByCategory(
            @Parameter(description = "Filter with category")
            @RequestParam(name = "id", required = true) Set<Long> ids){
        List<SubCategoryDTO> res = subCategoryService.getSubCategoriesByCategory(ids);
        if(res.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    "List empty.",
                    res
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list of sub category by categories successfully.",
                res
        ));
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For create SubCategory")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createSubCategory(
            @Valid @RequestBody SubCategoryFormRequest form
    ) {
        SubCategoryDTO res = subCategoryService.createSubCategory(form);
        if(res==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Create sub category failed.",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Create sub category successfully.",
                res
        ));
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For update SubCategory")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateSubCategory(
            @Parameter(description = "enter SubCategory id", required = true, example = "1") @NotNull @PathVariable(name = "id") Long id,
            @Valid @RequestBody SubCategoryFormRequest form
    ) {
        SubCategoryDTO res = subCategoryService.updateSubCategory(id, form);
        if(res==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Update sub category failed.",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Update sub category successfully.",
                res
        ));
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
        SubCategoryDTO res = subCategoryService.updateSubCategoryStatus(id, status);
        if(res==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Update sub category status failed.",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Update sub category status successfully.",
                res
        ));
    }

}
