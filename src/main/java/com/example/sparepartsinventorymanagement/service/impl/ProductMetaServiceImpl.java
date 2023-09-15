package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.CreateProductMetaForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateProductMetaForm;
import com.example.sparepartsinventorymanagement.entities.Category;
import com.example.sparepartsinventorymanagement.entities.Product;
import com.example.sparepartsinventorymanagement.entities.ProductMeta;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.ProductMetaRepository;
import com.example.sparepartsinventorymanagement.repository.ProductRepository;
import com.example.sparepartsinventorymanagement.service.ProductMetaService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import org.modelmapper.ModelMapper;
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

        Product product = productRepository.findById(productId).orElseThrow(
                ()-> new NotFoundException("Product not found")
        );

        List<ProductMeta> productMetas = productMetaRepository.findByProduct(product);
        if(productMetas.size() > 0){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(),"Get list successfully.", productMetas
            ));

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                HttpStatus.NOT_FOUND.toString(),"List empty", null
        ));
    }

    @Override
    public ResponseEntity getProductMetaById(Long id) {
        ProductMeta productMeta = productMetaRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Product meta not found")
        );
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),"Get product meta by id successfully.", productMeta
        ));
    }

    @Override
    public ResponseEntity createProductMeta(Long productId, CreateProductMetaForm form) {

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new NotFoundException("Product not found")
        );
        if(productMetaRepository.existsByKeyAndProduct(form.getKey(), product)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),"Key of product meta already exists", null
            ));
        }

        ModelMapper mapper = new ModelMapper();
        ProductMeta productMeta = mapper.map(form, ProductMeta.class);
        productMeta.setProduct(product);
        productMetaRepository.save(productMeta);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),"Create product meta successfully.", productMeta
        ));
    }

    @Override
    public ResponseEntity updateProductMeta(Long id, UpdateProductMetaForm form) {
        ProductMeta productMeta = productMetaRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Product meta not found")
        );
        if(productMetaRepository.existsByKeyAndProduct(form.getKey(), productMeta.getProduct())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),"Key of product meta already exists", null
            ));
        }
        productMeta.setKey(form.getKey());
        productMeta.setDescription(form.getDescription());
        productMetaRepository.save(productMeta);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),"Update product meta by id successfully.", productMeta
        ));
    }

    @Override
    public ResponseEntity deleteProductMeta(Long id) {
        ProductMeta productMeta = productMetaRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Product meta not found")
        );
        productMetaRepository.delete(productMeta);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),"Delete product meta successfully.", null
        ));
    }
}
