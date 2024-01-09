package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.CreateProductMetaForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateProductMetaForm;
import com.example.sparepartsinventorymanagement.dto.response.SubCategoryMetaDTO;

public interface SubCategoryMetaService {
    SubCategoryMetaDTO getSubCategoryMetaById(Long id);
    SubCategoryMetaDTO getSubCategoryMetaBySubCategoryId(Long subCateId);
    SubCategoryMetaDTO createSubCategoryMeta(Long subCateId, CreateProductMetaForm form);
    SubCategoryMetaDTO updateSubCategoryMeta(Long id,UpdateProductMetaForm form);
}
