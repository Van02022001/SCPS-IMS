package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.response.ProductDTO;
import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.*;
import com.example.sparepartsinventorymanagement.service.SubCategoryService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SubCategoryServiceImpl implements SubCategoryService {

    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private SizeRepository sizeRepository;
    @Autowired
    private UnitMeasurementRepository unitMeasurementRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public ResponseEntity<?> getAll() {
        List<SubCategory> subCategories = productRepository.findAll();
        if(subCategories.size() > 0){

            ModelMapper mapper = new ModelMapper();
            List<ProductDTO> res = mapper.map(subCategories, new TypeToken<List<ProductDTO>>() {
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
    public ResponseEntity<?> getSubCategoryById(Long id) {
        SubCategory subCategory = productRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Product not found")
        );
        ModelMapper mapper = new ModelMapper();
        ProductDTO res = mapper.map(subCategory, ProductDTO.class);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Get product by id successfully.", res
        ));
    }

    @Override
    public ResponseEntity<?> findByName(String name) {
        List<SubCategory> subCategories = productRepository.findByNameContaining(name);
        if(subCategories.size() > 0){
            ModelMapper mapper = new ModelMapper();
            List<ProductDTO> res = mapper.map(subCategories, new TypeToken<List<ProductDTO>>() {
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
    public ResponseEntity<?> getActiveProducts() {
        List<SubCategory> subCategories = productRepository.findByStatus(SubCategoryStatus.Active);
        if(subCategories.size() > 0){
            ModelMapper mapper = new ModelMapper();
            List<ProductDTO> res = mapper.map(subCategories, new TypeToken<List<ProductDTO>>() {
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
    public ResponseEntity<?> getProductsByCategory(Set<Long> ids) {
        List<Category> categories = new ArrayList<>();
        for (Long id: ids
             ) {
            Category category = categoryRepository.findById(id).orElseThrow(
                    ()-> new NotFoundException("Category not found")
            );
            categories.add(category);
        }

        List<SubCategory> subCategories = productRepository.findByCategoriesIn(categories);
        if(subCategories.size() > 0){
            ModelMapper mapper = new ModelMapper();
            List<ProductDTO> res = mapper.map(subCategories, new TypeToken<List<ProductDTO>>() {
            }.getType());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(), "Get list product by category successfully.", res
            ));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                HttpStatus.NOT_FOUND.toString(), "List empty.", null
        ));
    }

//    @Override
//    public ResponseEntity<?> createProduct(ProductFormRequest form) {
//
//        Set<Category> categories = new HashSet<>();
//        for (Long id : form.getCategories_id()
//             ) {
//            Category category = categoryRepository.findById(id).orElseThrow(
//                    ()-> new NotFoundException("Category not found")
//            );
//            if(category.getStatus() == CategoryStatus.Inactive){
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
//                        HttpStatus.BAD_REQUEST.toString(), "Category " + category.getName() + " was inactive." , null
//                ));
//            }
//           categories.add(category);
//        }
//        if(categories.size() < 1){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
//                    HttpStatus.BAD_REQUEST.toString(), "Product must have at least one category", null
//            ));
//        }
//
//        if(productRepository.existsByName(form.getName())){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
//               HttpStatus.BAD_REQUEST.toString(), "Product name already exists", null
//            ));
//        }
//        //check unit
//        Unit unit = unitRepository.findById(form.getUnit_id()).orElseThrow(
//                ()-> new NotFoundException("Unit not found")
//        );
//        //check unit of measurement id
//        UnitMeasurement unitMeasurement = unitMeasurementRepository.findById(form.getUnit_mea_id()).orElseThrow(
//                ()-> new NotFoundException("Unit of measurement not found")
//        );
//        Size size = Size.builder()
//                .height(form.getHeight())
//                .length(form.getLength())
//                .width(form.getWidth())
//                .diameter(form.getDiameter())
//                .unitMeasurement(unitMeasurement)
//                .build();
//        Date currentDate = new Date();
//        SubCategory subCategory = SubCategory.builder()
//                .name(form.getName())
//                .description(form.getDescription())
//                .minStockLevel(form.getMinStockLevel())
//                .maxStockLevel(form.getMaxStockLevel())
//                .createdAt(currentDate)
//                .updatedAt(currentDate)
//                .categories(categories)
//                .status(ProductStatus.Active)
//                .unit(unit)
//                .size(size)
//                .build();
//
//        size.setSubCategory(subCategory);
//        sizeRepository.save(size);
//        productRepository.save(subCategory);
//        ModelMapper mapper =  new ModelMapper();
//        ProductDTO res = mapper.map(subCategory, ProductDTO.class);
//        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
//                HttpStatus.OK.toString(), "Create product successfully.", res
//        ));
//    }

//    @Override
//    public ResponseEntity<?> updateProduct(Long id, ProductFormRequest form) {
//        Set<Category> categories = new HashSet<>();
//
//        SubCategory subCategory = productRepository.findById(id).orElseThrow(
//                ()-> new NotFoundException("Product not found")
//        );
//        for (Long ct_di : form.getCategories_id()
//        ) {
//            Category category = categoryRepository.findById(ct_di).orElseThrow(
//                    ()-> new NotFoundException("Category not found")
//            );
//            if(category.getStatus() == CategoryStatus.Inactive){
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
//                        HttpStatus.BAD_REQUEST.toString(), "Category " + category.getName() + " was inactive." , null
//                ));
//            }
//            categories.add(category);
//        }
//        if(categories.size() < 1){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
//                    HttpStatus.BAD_REQUEST.toString(), "Product must have at least one category", null
//            ));
//        }
//        if(!subCategory.getName().equalsIgnoreCase(form.getName())){
//            if(productRepository.existsByName(form.getName())){
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
//                        HttpStatus.BAD_REQUEST.toString(), "Product name already exists", null
//                ));
//            }
//        }
//        //check unit
//        Unit unit = unitRepository.findById(form.getUnit_id()).orElseThrow(
//                ()-> new NotFoundException("Unit not found")
//        );
//        //check unit of measurement id
//        UnitMeasurement unitMeasurement = unitMeasurementRepository.findById(form.getUnit_mea_id()).orElseThrow(
//                ()-> new NotFoundException("Unit of measurement not found")
//        );
//        Size size = sizeRepository.findByProduct(subCategory).orElseThrow(
//                ()-> new NotFoundException("Size of product not found")
//        );
//
//        subCategory.setName(form.getName());
//        subCategory.setDescription(form.getDescription());
//        subCategory.setMinStockLevel(form.getMinStockLevel());
//        subCategory.setMaxStockLevel(form.getMaxStockLevel());
//        subCategory.setCategories(categories);
//        Date currentDate = new Date();
//        subCategory.setUpdatedAt(currentDate);
//        subCategory.getSize().setHeight(form.getHeight());
//        subCategory.getSize().setWidth(form.getWidth());
//        subCategory.getSize().setLength(form.getLength());
//        subCategory.getSize().setDiameter(form.getDiameter());
//        subCategory.getSize().setUnitMeasurement(unitMeasurement);
//        subCategory.setUnit(unit);
//        sizeRepository.save(size);
//        productRepository.save(subCategory);
//        ModelMapper mapper = new ModelMapper();
//        ProductDTO res = mapper.map(subCategory, ProductDTO.class);
//        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
//                HttpStatus.OK.toString(), "Update product successfully.", res
//        ));
//    }

    @Override
    public ResponseEntity<?> updateProductStatus(Long id, SubCategoryStatus status) {
        SubCategory subCategory = productRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Product not found")
        );
        if(status == SubCategoryStatus.Active){
            subCategory.setStatus(SubCategoryStatus.Active);
        }else {
            subCategory.setStatus(SubCategoryStatus.Inactive);
        }
        productRepository.save(subCategory);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Update product status successfully.", null
        ));
    }
}
