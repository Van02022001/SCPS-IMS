package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.CreateProductForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateProductForm;
import com.example.sparepartsinventorymanagement.dto.response.ProductDTO;
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

import java.util.*;

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

            ModelMapper mapper = new ModelMapper();
            List<ProductDTO> res = new ArrayList<>();
            for (Product product : products
            ) {
                ProductDTO p = mapper.map(product, ProductDTO.class);
                res.add(p);
            }

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
               HttpStatus.OK.toString(), "Get list product successfully.", res
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
        ModelMapper mapper = new ModelMapper();
        ProductDTO res = mapper.map(product, ProductDTO.class);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Get product by id successfully.", res
        ));
    }

    @Override
    public ResponseEntity findByName(String name) {
        List<Product> products = productRepository.findByNameContaining(name);
        if(products.size() > 0){
            ModelMapper mapper = new ModelMapper();
            List<ProductDTO> res = new ArrayList<>();
            for (Product product : products
            ) {
                ProductDTO p = mapper.map(product, ProductDTO.class);
                res.add(p);
            }

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(), "Get list product by keyword " + name +" successfully.", res
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
            ModelMapper mapper = new ModelMapper();
            List<ProductDTO> res = new ArrayList<>();
            for (Product product : products
            ) {
                ProductDTO p = mapper.map(product, ProductDTO.class);
                res.add(p);
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(), "Get list active product successfully.", res
            ));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                HttpStatus.NOT_FOUND.toString(), "List empty.", null
        ));
    }

    @Override
    public ResponseEntity getProductsByCategory(Set<Long> ids) {
        List<Category> categories = new ArrayList<>();
        for (Long id: ids
             ) {
            Category category = categoryRepository.findById(id).orElseThrow(
                    ()-> new NotFoundException("Category not found")
            );
            categories.add(category);
        }

        List<Product> products = productRepository.findByCategoriesIn(categories);
        if(products.size() > 0){
            ModelMapper mapper = new ModelMapper();
            List<ProductDTO> res = new ArrayList<>();
            for (Product product : products
            ) {
                ProductDTO p = mapper.map(product, ProductDTO.class);
                res.add(p);
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(), "Get list product by category successfully.", res
            ));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                HttpStatus.NOT_FOUND.toString(), "List empty.", null
        ));
    }

    @Override
    public ResponseEntity createProduct(CreateProductForm form) {

        Set<Category> categories = new HashSet<>();
        for (Long id : form.getCategoryIds()
             ) {
            Category category = categoryRepository.findById(id).orElseThrow(
                    ()-> new NotFoundException("Category not found")
            );
            if(category.getStatus() == CategoryStatus.Inactive){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                        HttpStatus.BAD_REQUEST.toString(), "Category " + category.getName() + " was inactive." , null
                ));
            }
           categories.add(category);
        }
        if(categories.size() < 1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(), "Product must have at least one category", null
            ));
        }

        if(productRepository.existsByName(form.getName())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
               HttpStatus.BAD_REQUEST.toString(), "Product name already exists", null
            ));
        }
        ModelMapper mapper =  new ModelMapper();
        Product product =  mapper.map(form, Product.class);
        Date currentDate = new Date();
        product.setCreatedAt(currentDate);
        product.setUpdatedAt(currentDate);
        product.setCategories(categories);
        product.setStatus(ProductStatus.Active);
        productRepository.save(product);

        ProductDTO res = mapper.map(product, ProductDTO.class);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Create product successfully.", res
        ));
    }

    @Override
    public ResponseEntity updateProduct(Long id, UpdateProductForm form) {
        Set<Category> categories = new HashSet<>();
        for (Long cateId : form.getCategoryIds()
        ) {
            Category category = categoryRepository.findById(cateId).orElseThrow(
                    ()-> new NotFoundException("Category not found")
            );
            if(category.getStatus() == CategoryStatus.Inactive){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                        HttpStatus.BAD_REQUEST.toString(), "Category " + category.getName() + " was inactive." , null
                ));
            }
            categories.add(category);
        }
        if(categories.size() < 1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(), "Product must have at least one category", null
            ));
        }
        Product product = productRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Product not found")
        );
        product.setName(form.getName());
        product.setDescription(form.getDescription());
        product.setCategories(categories);
        Date currentDate = new Date();
        product.setUpdatedAt(currentDate);
        productRepository.save(product);
        ModelMapper mapper = new ModelMapper();
        ProductDTO res = mapper.map(product, ProductDTO.class);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Update product successfully.", res
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
        productRepository.save(product);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Update product status successfully.", null
        ));
    }
}
