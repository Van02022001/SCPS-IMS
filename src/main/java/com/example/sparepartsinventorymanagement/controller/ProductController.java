package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.CreateCategoryForm;
import com.example.sparepartsinventorymanagement.dto.request.CreateProductForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateCategoryForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateProductForm;
import com.example.sparepartsinventorymanagement.entities.CategoryStatus;
import com.example.sparepartsinventorymanagement.entities.ProductStatus;
import com.example.sparepartsinventorymanagement.service.impl.ProductServiceImpl;
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
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    @Autowired
    private ProductServiceImpl productService;

    @Operation(summary = "For get list of products")
    @GetMapping(value = "/getAllProduct", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        return productService.getAll();
    }

    @Operation(summary = "For get product by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCategoryById(
            @Parameter(description = "enter product id to get", example = "1", required = true) @PathVariable(name = "id") @NotBlank Long id
    ) {
        return productService.getProductById(id);
    }

    @Operation(summary = "For get products contains keyword by name")
    @GetMapping(value = "/getProductsByName", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCategoryByName(
            @Parameter(description = "enter keyword to search", required = true)
            @NotEmpty @NotBlank String keyword
    ) {
        return productService.findByName(keyword);
    }

    @Operation(summary = "For get product by active status")
    @GetMapping(value = "/getActiveProducts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getActiveProducts() {
        return productService.getActiveProducts();
    }

    @Operation(summary = "For get product by category")
    @GetMapping(value = "/getProductsByCategory", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductsByCategory(
            @Parameter(description = "Filter with category")
            @RequestParam(name = "id", required = true) Set<Long> ids){
        return productService.getProductsByCategory(ids);
    }

    @Operation(summary = "For create product")
    @PostMapping(value = "/createProduct", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCategory(
            @Valid @RequestBody CreateProductForm form
    ) {
        return productService.createProduct(form);
    }
    @Operation(summary = "For update product")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateCategory(
            @Parameter(description = "enter product id", required = true, example = "1") @NotNull @PathVariable(name = "id") Long id,
            @Valid @RequestBody UpdateProductForm form
    ) {
        return productService.updateProduct(id, form);
    }

    @Operation(summary = "For update status of product")
    @PutMapping(value = "/changeProductStatus/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateCategoryStatus(
            @Parameter(description = "enter product id", required = true, example = "1")
            @NotNull @NotEmpty @PathVariable(name = "id") Long id,
            @Parameter(description = "Product status (Active or Inactive)", required = true)
            @NotNull @NotEmpty @Pattern(regexp = "Active|Inactive") @RequestParam(name = "status") ProductStatus status
    ) {
        return productService.updateProductStatus(id, status);
    }

}
