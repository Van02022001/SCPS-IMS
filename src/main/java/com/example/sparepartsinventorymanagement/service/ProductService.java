package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.CreateProductForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateProductForm;
import com.example.sparepartsinventorymanagement.entities.Category;
import com.example.sparepartsinventorymanagement.entities.ProductStatus;
import org.springframework.http.ResponseEntity;

public interface ProductService {
    ResponseEntity getAll();
    ResponseEntity getProductById(Long id);
    ResponseEntity findByName(String name);
    ResponseEntity getActiveProducts();
    ResponseEntity getProductsByCategory(Long categoryId);
    ResponseEntity createProduct(CreateProductForm form);
    ResponseEntity updateProduct(Long id,UpdateProductForm form);
    ResponseEntity updateProductStatus(Long id,ProductStatus status);
}
