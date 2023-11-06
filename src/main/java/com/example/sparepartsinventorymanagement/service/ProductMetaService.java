package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.CreateProductMetaForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateProductMetaForm;
import org.springframework.http.ResponseEntity;

public interface ProductMetaService {
    ResponseEntity getAllByProduct(Long productId);
    ResponseEntity getProductMetaById(Long id);
    ResponseEntity createProductMeta(Long id, CreateProductMetaForm form);
    ResponseEntity updateProductMeta(Long id,UpdateProductMetaForm form);
    ResponseEntity deleteProductMeta(Long id);
}
