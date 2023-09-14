package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.CreateProductForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateProductForm;
import com.example.sparepartsinventorymanagement.entities.Category;
import com.example.sparepartsinventorymanagement.entities.CategoryStatus;
import com.example.sparepartsinventorymanagement.entities.Product;
import com.example.sparepartsinventorymanagement.entities.ProductStatus;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.CategoryRepository;
import com.example.sparepartsinventorymanagement.repository.ProductRepository;
import com.example.sparepartsinventorymanagement.service.ProductService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public ResponseEntity getAll() {
        List<Product> products = productRepository.findAll();
        if(products.size() > 0){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
               HttpStatus.OK.toString(), "Get list product successfully.", products
            ));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                HttpStatus.NOT_FOUND.toString(), "List empty.", null
        ));
    }

    @Override
    public ResponseEntity getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Product not found")
        );
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Get product by id successfully.", product
        ));
    }

    @Override
    public ResponseEntity findByName(String name) {
        List<Product> products = productRepository.findByNameContaining(name);
        if(products.size() > 0){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(), "Get list product by keyword " + name +" successfully.", products
            ));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                HttpStatus.NOT_FOUND.toString(), "List empty.", null
        ));
    }

    @Override
    public ResponseEntity getActiveProducts() {
        List<Product> products = productRepository.findByStatus(ProductStatus.Active);
        if(products.size() > 0){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(), "Get list active product successfully.", products
            ));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                HttpStatus.NOT_FOUND.toString(), "List empty.", null
        ));
    }

    @Override
    public ResponseEntity getProductsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                ()-> new NotFoundException("Category not found")
        );
        List<Product> products = productRepository.findByCategory(category);
        if(products.size() > 0){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(), "Get list active product successfully.", products
            ));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                HttpStatus.NOT_FOUND.toString(), "List empty.", null
        ));
    }

    @Override
    public ResponseEntity createProduct(CreateProductForm form) {
        Category category = categoryRepository.findById(form.getCategoryId()).orElseThrow(
                ()-> new NotFoundException("Category not found")
        );
        if(categoryRepository.existsByName(form.getName())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
               HttpStatus.BAD_REQUEST.toString(), "Product name already exists", null
            ));
        }
        ModelMapper mapper =  new ModelMapper();
        Product product =  mapper.map(form, Product.class);
        Date currentDate = new Date();
        product.setCreatedAt(currentDate);
        product.setUpdatedAt(currentDate);
        product.setCategory(category);
        product.setStatus(ProductStatus.Active);
        productRepository.save(product);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Create product successfully.", product
        ));
    }

    @Override
    public ResponseEntity updateProduct(Long id, UpdateProductForm form) {
        Category category = categoryRepository.findById(form.getCategoryId()).orElseThrow(
                ()-> new NotFoundException("Category not found")
        );
        if(category.getStatus() == CategoryStatus.Inactive){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(), "Category has been inactive.", null
            ));
        }
        Product product = productRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Product not found")
        );
        product.setName(form.getName());
        product.setDescription(form.getDescription());
        product.setCategory(category);
        Date currentDate = new Date();
        product.setUpdatedAt(currentDate);
        productRepository.save(product);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Update product successfully.", product
        ));
    }

    @Override
    public ResponseEntity updateProductStatus(Long id, ProductStatus status) {
        Product product = productRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Product not found")
        );
        if(status == ProductStatus.Active){
            product.setStatus(ProductStatus.Active);
        }else {
            product.setStatus(ProductStatus.Inactive);
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Update product status successfully.", null
        ));
    }
}
