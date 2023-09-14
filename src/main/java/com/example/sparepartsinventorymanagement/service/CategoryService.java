package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.CreateCategoryForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateCategoryForm;
import com.example.sparepartsinventorymanagement.entities.CategoryStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

public interface CategoryService {
    ResponseEntity getAll();
    ResponseEntity getCategoryById(Long id);
    ResponseEntity searchCategoryByName(String name);
    ResponseEntity createCategory(CreateCategoryForm form);

    ResponseEntity updateCategory(Long id, UpdateCategoryForm form);

    ResponseEntity updateCategoryStatus(Long id, CategoryStatus status);
    ResponseEntity getActiveCategories();
}
