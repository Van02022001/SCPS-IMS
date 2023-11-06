package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.CreateProductMetaForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateProductMetaForm;
import com.example.sparepartsinventorymanagement.entities.SubCategory;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.SubCategoryMeta;
import com.example.sparepartsinventorymanagement.repository.SubCategoryRepository;
import com.example.sparepartsinventorymanagement.service.SubCategoryMetaService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubCategoryMetaServiceImpl implements SubCategoryMetaService {

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private SubCategoryMeta subCategoryMeta;

    @Override
    public ResponseEntity<?> getAllBySubCategory(Long productId) {

        SubCategory subCategory = subCategoryRepository.findById(productId).orElseThrow(
                ()-> new NotFoundException("Product not found")
        );

        List<com.example.sparepartsinventorymanagement.entities.SubCategoryMeta> subCategoryMetas = subCategoryMeta.findBySubCategory(subCategory);
        if(subCategoryMetas.size() > 0){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(),"Get list successfully.", subCategoryMetas
            ));

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                HttpStatus.NOT_FOUND.toString(),"List empty", null
        ));
    }

    @Override
    public ResponseEntity<?> getSubCategoryMetaById(Long id) {
        com.example.sparepartsinventorymanagement.entities.SubCategoryMeta subCategoryMeta = this.subCategoryMeta.findById(id).orElseThrow(
                ()-> new NotFoundException("Product meta not found")
        );
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),"Get product meta by id successfully.", subCategoryMeta
        ));
    }

    @Override
    public ResponseEntity<?> createSubCategoryMeta(Long productId, CreateProductMetaForm form) {

        SubCategory subCategory = subCategoryRepository.findById(productId).orElseThrow(
                () -> new NotFoundException("Product not found")
        );
        if(subCategoryMeta.existsByKeyAndSubCategory(form.getKey(), subCategory)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),"Key of product meta already exists", null
            ));
        }
        com.example.sparepartsinventorymanagement.entities.SubCategoryMeta subCategoryMeta = com.example.sparepartsinventorymanagement.entities.SubCategoryMeta.builder()
                .key(form.getKey())
                .description(form.getDescription())
                .subCategory(subCategory)
                .build();
        this.subCategoryMeta.save(subCategoryMeta);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),"Create product meta successfully.", subCategoryMeta
        ));
    }

    @Override
    public ResponseEntity<?> updateSubCategoryMeta(Long id, UpdateProductMetaForm form) {
        com.example.sparepartsinventorymanagement.entities.SubCategoryMeta subCategoryMeta = this.subCategoryMeta.findById(id).orElseThrow(
                ()-> new NotFoundException("Product meta not found")
        );
        if(this.subCategoryMeta.existsByKeyAndSubCategory(form.getKey(), subCategoryMeta.getSubCategory())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),"Key of product meta already exists", null
            ));
        }
        subCategoryMeta.setKey(form.getKey());
        subCategoryMeta.setDescription(form.getDescription());
        this.subCategoryMeta.save(subCategoryMeta);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),"Update product meta by id successfully.", subCategoryMeta
        ));
    }

    @Override
    public ResponseEntity<?> deleteSubCategoryMeta(Long id) {
        com.example.sparepartsinventorymanagement.entities.SubCategoryMeta subCategoryMeta = this.subCategoryMeta.findById(id).orElseThrow(
                ()-> new NotFoundException("Product meta not found")
        );
        this.subCategoryMeta.delete(subCategoryMeta);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),"Delete product meta successfully.", null
        ));
    }
}
