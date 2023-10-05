package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.ProductFormRequest;
import com.example.sparepartsinventorymanagement.dto.request.UpdateProductForm;
import com.example.sparepartsinventorymanagement.dto.response.ProductDTO;
import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.*;
import com.example.sparepartsinventorymanagement.service.ProductService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private SizeRepository sizeRepository;
    @Autowired
    private UnitMeasurementRepository unitMeasurementRepository;
    @Autowired
    private  OriginRepository originRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public ResponseEntity<?> getAll() {
        List<Product> products = productRepository.findAll();
        if(products.size() > 0){

            ModelMapper mapper = new ModelMapper();
            List<ProductDTO> res = mapper.map(products, new TypeToken<List<ProductDTO>>() {
            }.getType());
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
            List<ProductDTO> res = mapper.map(products, new TypeToken<List<ProductDTO>>() {
            }.getType());
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
            List<ProductDTO> res = mapper.map(products, new TypeToken<List<ProductDTO>>() {
            }.getType());
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
            List<ProductDTO> res = mapper.map(products, new TypeToken<List<ProductDTO>>() {
            }.getType());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(), "Get list product by category successfully.", res
            ));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                HttpStatus.NOT_FOUND.toString(), "List empty.", null
        ));
    }

    @Override
    public ResponseEntity createProduct(ProductFormRequest form) {

        Set<Category> categories = new HashSet<>();
        for (Long id : form.getCategories_id()
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

        //check origin
        Set<Origin> origins = new HashSet<>();
        for (Long id : form.getOrigins_id()
        ) {
            Origin origin = originRepository.findById(id).orElseThrow(
                    ()-> new NotFoundException("Origin not found")
            );
            origins.add(origin);
        }
        if(origins.size() < 1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(), "Product must have at least one origin", null
            ));
        }

        if(productRepository.existsByName(form.getName())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
               HttpStatus.BAD_REQUEST.toString(), "Product name already exists", null
            ));
        }
        //check unit
        Unit unit = unitRepository.findById(form.getUnit_id()).orElseThrow(
                ()-> new NotFoundException("Unit not found")
        );
        //check unit of measurement id
        UnitMeasurement unitMeasurement = unitMeasurementRepository.findById(form.getUnit_mea_id()).orElseThrow(
                ()-> new NotFoundException("Unit of measurement not found")
        );
        Size size = Size.builder()
                .height(form.getHeight())
                .length(form.getLength())
                .width(form.getWidth())
                .unitMeasurement(unitMeasurement)
                .build();
        ModelMapper mapper =  new ModelMapper();
        Product product =  mapper.map(form, Product.class);
        Date currentDate = new Date();
        product.setCreatedAt(currentDate);
        product.setUpdatedAt(currentDate);
        product.setCategories(categories);
        product.setStatus(ProductStatus.Active);
        product.setOrigins(origins);
        product.setUnit(unit);
        product.setSize(size);

        size.setProduct(product);
        sizeRepository.save(size);
        productRepository.save(product);

        ProductDTO res = mapper.map(product, ProductDTO.class);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Create product successfully.", res
        ));
    }

    @Override
    public ResponseEntity updateProduct(Long id, ProductFormRequest form) {
        Set<Category> categories = new HashSet<>();

        Product product = productRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Product not found")
        );
        for (Long ct_di : form.getCategories_id()
        ) {
            Category category = categoryRepository.findById(ct_di).orElseThrow(
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

        //check origin
        Set<Origin> origins = new HashSet<>();
        for (Long or_id : form.getOrigins_id()
        ) {
            Origin origin = originRepository.findById(or_id).orElseThrow(
                    ()-> new NotFoundException("Origin not found")
            );
            origins.add(origin);
        }
        if(origins.size() < 1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(), "Product must have at least one origin", null
            ));
        }

        if(productRepository.existsByName(form.getName())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(), "Product name already exists", null
            ));
        }
        //check unit
        Unit unit = unitRepository.findById(form.getUnit_id()).orElseThrow(
                ()-> new NotFoundException("Unit not found")
        );
        //check unit of measurement id
        UnitMeasurement unitMeasurement = unitMeasurementRepository.findById(form.getUnit_mea_id()).orElseThrow(
                ()-> new NotFoundException("Unit of measurement not found")
        );
        Size size = sizeRepository.findByProduct(product).orElseThrow(
                ()-> new NotFoundException("Size of product not found")
        );

        product.setName(form.getName());
        product.setDescription(form.getDescription());
        product.setMinStockLevel(form.getMinStockLevel());
        product.setMaxStockLevel(form.getMaxStockLevel());
        product.setCategories(categories);
        Date currentDate = new Date();
        product.setUpdatedAt(currentDate);
        product.setOrigins(origins);
        product.getSize().setHeight(form.getHeight());
        product.getSize().setWidth(form.getWidth());
        product.getSize().setLength(form.getLength());
        product.setUnit(unit);
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
