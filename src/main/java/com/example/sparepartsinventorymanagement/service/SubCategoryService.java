package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.SubCategoryFormRequest;
import com.example.sparepartsinventorymanagement.entities.SubCategoryStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface SubCategoryService {
    ResponseEntity<?> getAll();
    ResponseEntity<?> getSubCategoryById(Long id);
    ResponseEntity<?> findByName(String name);
    ResponseEntity<?> getActiveSubCategories();
    ResponseEntity<?> getSubCategoriesByCategory(Set<Long> categoryIds);
    ResponseEntity<?> createSubCategory(SubCategoryFormRequest form);
    ResponseEntity<?> updateSubCategory(Long id,SubCategoryFormRequest form);
    ResponseEntity<?> updateSubCategoryStatus(Long id, SubCategoryStatus status);
}
