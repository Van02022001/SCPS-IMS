package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.CreateProductMetaForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateProductMetaForm;
import com.example.sparepartsinventorymanagement.dto.response.SubCategoryMetaDTO;
import com.example.sparepartsinventorymanagement.service.impl.SubCategoryMetaServiceImpl;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sub-category-metas")
@Tag(name = "sub-category-meta")
public class SubCategoryMetaController {
    @Autowired
    private SubCategoryMetaServiceImpl productMetaService;


    @Operation(summary = "For sub category meta by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductMetaById(
            @Parameter(description = "Enter sub category meta id to get", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id
    ) {
        SubCategoryMetaDTO res = productMetaService.getSubCategoryMetaById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
           HttpStatus.OK.toString(),
           "Get sub category meta successfully",
           res
        ));
    }
    @Operation(summary = "For sub category meta by sub category id")
    @GetMapping(value = "/sub-category/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSubCateMetaByCateogory(
            @Parameter(description = "Enter sub category id to get", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id
    ) {
        SubCategoryMetaDTO res = productMetaService.getSubCategoryMetaBySubCategoryId(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get sub category meta successfully",
                res
        ));
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For create sub category meta")
    @PostMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createProductMeta(
            @Parameter(description = "Enter sub category id to get", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id,
            @Valid @RequestBody CreateProductMetaForm form
    ) {
        SubCategoryMetaDTO res = productMetaService.createSubCategoryMeta(id, form);
        if(res == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Sub category has sub category meta",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Create sub category meta successfully",
                res
        ));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For update sub category meta")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProductMeta(
            @Parameter(description = "Enter sub category id to get", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id,
            @Valid @RequestBody UpdateProductMetaForm form
    ) {
        SubCategoryMetaDTO res = productMetaService.updateSubCategoryMeta(id, form);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Update sub category meta successfully",
                res
        ));
    }
}
