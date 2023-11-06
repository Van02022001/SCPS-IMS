package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.CreateProductMetaForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateProductMetaForm;
import com.example.sparepartsinventorymanagement.entities.SubCategory;
import com.example.sparepartsinventorymanagement.entities.SubCategoryMeta;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.ProductMetaRepository;
import com.example.sparepartsinventorymanagement.repository.ProductRepository;
import com.example.sparepartsinventorymanagement.service.ProductMetaService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductMetaServiceImpl implements ProductMetaService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMetaRepository productMetaRepository;

    @Override
    public ResponseEntity getAllByProduct(Long productId) {

        SubCategory subCategory = productRepository.findById(productId).orElseThrow(
                ()-> new NotFoundException("Product not found")
        );

        List<SubCategoryMeta> subCategoryMetas = productMetaRepository.findByProduct(subCategory);
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
    public ResponseEntity getProductMetaById(Long id) {
        SubCategoryMeta subCategoryMeta = productMetaRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Product meta not found")
        );
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),"Get product meta by id successfully.", subCategoryMeta
        ));
    }

    @Override
    public ResponseEntity createProductMeta(Long productId, CreateProductMetaForm form) {

        SubCategory subCategory = productRepository.findById(productId).orElseThrow(
                () -> new NotFoundException("Product not found")
        );
        if(productMetaRepository.existsByKeyAndProduct(form.getKey(), subCategory)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),"Key of product meta already exists", null
            ));
        }
        SubCategoryMeta subCategoryMeta = SubCategoryMeta.builder()
                .key(form.getKey())
                .description(form.getDescription())
                .subCategory(subCategory)
                .build();
        productMetaRepository.save(subCategoryMeta);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),"Create product meta successfully.", subCategoryMeta
        ));
    }

    @Override
    public ResponseEntity updateProductMeta(Long id, UpdateProductMetaForm form) {
        SubCategoryMeta subCategoryMeta = productMetaRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Product meta not found")
        );
        if(productMetaRepository.existsByKeyAndProduct(form.getKey(), subCategoryMeta.getSubCategory())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),"Key of product meta already exists", null
            ));
        }
        subCategoryMeta.setKey(form.getKey());
        subCategoryMeta.setDescription(form.getDescription());
        productMetaRepository.save(subCategoryMeta);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),"Update product meta by id successfully.", subCategoryMeta
        ));
    }

    @Override
    public ResponseEntity deleteProductMeta(Long id) {
        SubCategoryMeta subCategoryMeta = productMetaRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Product meta not found")
        );
        productMetaRepository.delete(subCategoryMeta);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),"Delete product meta successfully.", null
        ));
    }
}
