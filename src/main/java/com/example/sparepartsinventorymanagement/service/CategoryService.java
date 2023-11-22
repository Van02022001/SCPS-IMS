package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.CategoryFormRequest;
import com.example.sparepartsinventorymanagement.dto.response.GetCategoryDTO;
import com.example.sparepartsinventorymanagement.entities.CategoryStatus;

import java.util.List;

public interface CategoryService {
    List<GetCategoryDTO> getAll();
    GetCategoryDTO getCategoryById(Long id);
    List<GetCategoryDTO> searchCategoryByName(String name);
    GetCategoryDTO createCategory(CategoryFormRequest form);
    GetCategoryDTO updateCategory(Long id, CategoryFormRequest form);
    GetCategoryDTO updateCategoryStatus(Long id, CategoryStatus status);
    List<GetCategoryDTO> getActiveCategories();
}
