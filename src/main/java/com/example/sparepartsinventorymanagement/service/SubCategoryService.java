package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.entities.SubCategoryStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface SubCategoryService {
    ResponseEntity<?> getAll();
    ResponseEntity<?> getSubCategoryById(Long id);
    ResponseEntity<?> findByName(String name);
    ResponseEntity<?> getActiveProducts();
    ResponseEntity<?> getProductsByCategory(Set<Long> categoryIds);
    //ResponseEntity<?> createProduct(ProductFormRequest form);
    //ResponseEntity<?> updateProduct(Long id,ProductFormRequest form);
    ResponseEntity<?> updateProductStatus(Long id, SubCategoryStatus status);
}
