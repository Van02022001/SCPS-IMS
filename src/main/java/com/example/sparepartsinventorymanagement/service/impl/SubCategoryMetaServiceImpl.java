package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.CreateProductMetaForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateProductMetaForm;
import com.example.sparepartsinventorymanagement.dto.response.SubCategoryMetaDTO;
import com.example.sparepartsinventorymanagement.entities.SubCategory;
import com.example.sparepartsinventorymanagement.entities.SubCategoryMeta;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.SubCategoryMetaRepository;
import com.example.sparepartsinventorymanagement.repository.SubCategoryRepository;
import com.example.sparepartsinventorymanagement.service.SubCategoryMetaService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubCategoryMetaServiceImpl implements SubCategoryMetaService {

    private final SubCategoryRepository subCategoryRepository;

    private final SubCategoryMetaRepository subCategoryMetaRepository;

    private final ModelMapper mapper;
    @Override
    public SubCategoryMetaDTO getSubCategoryMetaById(Long id) {

        SubCategoryMeta subCategoryMeta = subCategoryMetaRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Product meta not found")
        );
        return mapper.map(subCategoryMeta, SubCategoryMetaDTO.class);
    }

    @Override
    public SubCategoryMetaDTO getSubCategoryMetaBySubCategoryId(Long subCateId) {
        SubCategory subCategory = subCategoryRepository.findById(subCateId).orElseThrow(
                () -> new NotFoundException("Sub category not found")
        );
        SubCategoryMeta subCategoryMeta = subCategoryMetaRepository.findBySubCategory(subCategory).orElseThrow(
                ()-> new NotFoundException("Product meta not found")
        );
        return mapper.map(subCategoryMeta, SubCategoryMetaDTO.class);
    }

    @Override
    public SubCategoryMetaDTO createSubCategoryMeta(Long productId, CreateProductMetaForm form) {

        SubCategory subCategory = subCategoryRepository.findById(productId).orElseThrow(
                () -> new NotFoundException("Product not found")
        );
        if(subCategory.getSubCategoryMeta() != null){
            return null;
        }
        SubCategoryMeta subCategoryMeta = SubCategoryMeta.builder()
                .key(form.getKey().trim())
                .description(form.getDescription().trim())
                .subCategory(subCategory)
                .build();
        subCategoryMetaRepository.save(subCategoryMeta);
        subCategory.setSubCategoryMeta(subCategoryMeta);
        subCategoryRepository.save(subCategory);
        return mapper.map(subCategoryMeta, SubCategoryMetaDTO.class);
    }

    @Override
    public SubCategoryMetaDTO updateSubCategoryMeta(Long id, UpdateProductMetaForm form) {
        SubCategoryMeta subCategoryMeta = subCategoryMetaRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Product meta not found")
        );

        subCategoryMeta.setKey(form.getKey());
        subCategoryMeta.setDescription(form.getDescription());
        subCategoryMetaRepository.save(subCategoryMeta);
        return mapper.map(subCategoryMeta, SubCategoryMetaDTO.class);
    }

}
