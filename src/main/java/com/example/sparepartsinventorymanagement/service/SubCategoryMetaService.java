package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.CreateProductMetaForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateProductMetaForm;
import org.springframework.http.ResponseEntity;

public interface SubCategoryMetaService {
    ResponseEntity<?> getAllBySubCategory(Long productId);
    ResponseEntity<?> getSubCategoryMetaById(Long id);
    ResponseEntity<?> createSubCategoryMeta(Long id, CreateProductMetaForm form);
    ResponseEntity<?> updateSubCategoryMeta(Long id,UpdateProductMetaForm form);
    ResponseEntity<?> deleteSubCategoryMeta(Long id);
}
