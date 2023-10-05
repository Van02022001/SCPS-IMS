package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.ProductFormRequest;
import com.example.sparepartsinventorymanagement.entities.ProductStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface ProductService {
    ResponseEntity<?> getAll();
    ResponseEntity<?> getProductById(Long id);
    ResponseEntity<?> findByName(String name);
    ResponseEntity<?> getActiveProducts();
    ResponseEntity<?> getProductsByCategory(Set<Long> categoryIds);
    ResponseEntity<?> createProduct(ProductFormRequest form);
    ResponseEntity<?> updateProduct(Long id,ProductFormRequest form);
    ResponseEntity<?> updateProductStatus(Long id,ProductStatus status);
}
