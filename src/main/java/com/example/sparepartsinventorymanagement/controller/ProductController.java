package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.ProductFormRequest;
import com.example.sparepartsinventorymanagement.entities.ProductStatus;
import com.example.sparepartsinventorymanagement.service.impl.SubCategoryServiceImpl;
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

import java.util.Set;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    @Autowired
    private SubCategoryServiceImpl productService;

    @Operation(summary = "For get list of products")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        return productService.getAll();
    }

    @Operation(summary = "For get product by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductById(
            @Parameter(description = "enter product id to get", example = "1", required = true) @PathVariable(name = "id") @NotBlank Long id
    ) {
        return productService.getSubCategoryById(id);
    }
    @Operation(summary = "For get products contains keyword by name")
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductByName(
            @Parameter(description = "enter keyword to search", required = true)
            @NotEmpty @NotBlank String keyword
    ) {
        return productService.findByName(keyword);
    }
    @Operation(summary = "For get product by active status")
    @GetMapping(value = "/active-products", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getActiveProducts() {
        return productService.getActiveProducts();
    }

    @Operation(summary = "For get product by category")
    @GetMapping(value = "/products-by-categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductsByCategory(
            @Parameter(description = "Filter with category")
            @RequestParam(name = "id", required = true) Set<Long> ids){
        return productService.getProductsByCategory(ids);
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For create product")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createProduct(
            @Valid @RequestBody ProductFormRequest form
    ) {
        return productService.createProduct(form);
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For update product")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProduct(
            @Parameter(description = "enter product id", required = true, example = "1") @NotNull @PathVariable(name = "id") Long id,
            @Valid @RequestBody ProductFormRequest form
    ) {
        return productService.updateProduct(id, form);
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For update status of product")
    @PutMapping(value = "/product-status/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProductStatus(
            @Parameter(description = "enter product id", required = true, example = "1")
            @NotNull @NotEmpty @PathVariable(name = "id") Long id,
            @Parameter(description = "Product status (Active or Inactive)", required = true)
            @NotNull @NotEmpty @Pattern(regexp = "Active|Inactive") @RequestParam(name = "status") ProductStatus status
    ) {
        return productService.updateProductStatus(id, status);
    }

}
