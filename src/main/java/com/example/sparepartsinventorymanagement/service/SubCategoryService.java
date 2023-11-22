package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.SubCategoryFormRequest;
import com.example.sparepartsinventorymanagement.dto.response.SubCategoryDTO;
import com.example.sparepartsinventorymanagement.entities.SubCategoryStatus;

import java.util.List;
import java.util.Set;

public interface SubCategoryService {
    List<SubCategoryDTO> getAll();
    SubCategoryDTO getSubCategoryById(Long id);
    List<SubCategoryDTO> findByName(String name);
    List<SubCategoryDTO> getActiveSubCategories();
    List<SubCategoryDTO> getSubCategoriesByCategory(Set<Long> categoryIds);
    SubCategoryDTO createSubCategory(SubCategoryFormRequest form);
    SubCategoryDTO updateSubCategory(Long id,SubCategoryFormRequest form);
    SubCategoryDTO updateSubCategoryStatus(Long id, SubCategoryStatus status);
}
