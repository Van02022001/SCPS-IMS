package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.SubCategoryFormRequest;
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
    private SubCategoryRepository subCategoryRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public ResponseEntity<?> getAll() {
        List<SubCategory> subCategories = subCategoryRepository.findAll();
        if(!subCategories.isEmpty()){

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
        SubCategory subCategory = subCategoryRepository.findById(id).orElseThrow(
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
        List<SubCategory> subCategories = subCategoryRepository.findByNameContaining(name);
        if(!subCategories.isEmpty()){
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
    public ResponseEntity<?> getActiveSubCategories() {
        List<SubCategory> subCategories = subCategoryRepository.findByStatus(SubCategoryStatus.Active);
        if(!subCategories.isEmpty()){
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
    public ResponseEntity<?> getSubCategoriesByCategory(Set<Long> ids) {
        List<Category> categories = new ArrayList<>();
        for (Long id: ids
             ) {
            Category category = categoryRepository.findById(id).orElseThrow(
                    ()-> new NotFoundException("Category not found")
            );
            categories.add(category);
        }

        List<SubCategory> subCategories = subCategoryRepository.findByCategoriesIn(categories);
        if(!subCategories.isEmpty()){
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

    @Override
    public ResponseEntity<?> createSubCategory(SubCategoryFormRequest form) {

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
        if(categories.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(), "SubCategory must have at least one category", null
            ));
        }

        if(subCategoryRepository.existsByName(form.getName())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
               HttpStatus.BAD_REQUEST.toString(), "SubCategory name already exists", null
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
                .diameter(form.getDiameter())
                .unitMeasurement(unitMeasurement)
                .build();
        Date currentDate = new Date();
        SubCategory subCategory = SubCategory.builder()
                .name(form.getName())
                .description(form.getDescription())
                .createdAt(currentDate)
                .updatedAt(currentDate)
                .categories(categories)
                .status(SubCategoryStatus.Active)
                .unit(unit)
                .size(size)
                .build();


        size.setSubCategory(subCategory);
        sizeRepository.save(size);
        subCategoryRepository.save(subCategory);
        for (Category category: categories
        ) {
            category.getSubCategories().add(subCategory);
            categoryRepository.save(category);
        }
        ModelMapper mapper =  new ModelMapper();
        ProductDTO res = mapper.map(subCategory, ProductDTO.class);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Create SubCategory successfully.", res
        ));
    }

    @Override
    public ResponseEntity<?> updateSubCategory(Long id, SubCategoryFormRequest form) {
        Set<Category> categories = new HashSet<>();

        SubCategory subCategory = subCategoryRepository.findById(id).orElseThrow(
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
        if(categories.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(), "Product must have at least one category", null
            ));
        }
        if(!subCategory.getName().equalsIgnoreCase(form.getName())){
            if(subCategoryRepository.existsByName(form.getName())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                        HttpStatus.BAD_REQUEST.toString(), "Product name already exists", null
                ));
            }
        }
        //check unit
        Unit unit = unitRepository.findById(form.getUnit_id()).orElseThrow(
                ()-> new NotFoundException("Unit not found")
        );
        //check unit of measurement id
        UnitMeasurement unitMeasurement = unitMeasurementRepository.findById(form.getUnit_mea_id()).orElseThrow(
                ()-> new NotFoundException("Unit of measurement not found")
        );
        Size size = sizeRepository.findBySubCategory(subCategory).orElseThrow(
                ()-> new NotFoundException("Size of SubCategory not found")
        );

        subCategory.setName(form.getName());
        subCategory.setDescription(form.getDescription());
        subCategory.setCategories(categories);
        Date currentDate = new Date();
        subCategory.setUpdatedAt(currentDate);
        subCategory.getSize().setHeight(form.getHeight());
        subCategory.getSize().setWidth(form.getWidth());
        subCategory.getSize().setLength(form.getLength());
        subCategory.getSize().setDiameter(form.getDiameter());
        subCategory.getSize().setUnitMeasurement(unitMeasurement);
        subCategory.setUnit(unit);
        sizeRepository.save(size);
        subCategoryRepository.save(subCategory);
        ModelMapper mapper = new ModelMapper();
        ProductDTO res = mapper.map(subCategory, ProductDTO.class);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Update SubCategory successfully.", res
        ));
    }

    @Override
    public ResponseEntity<?> updateSubCategoryStatus(Long id, SubCategoryStatus status) {
        SubCategory subCategory = subCategoryRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Product not found")
        );
        if(status == SubCategoryStatus.Active){
            subCategory.setStatus(SubCategoryStatus.Active);
        }else {
            subCategory.setStatus(SubCategoryStatus.Inactive);
        }
        subCategoryRepository.save(subCategory);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Update SubCategory status successfully.", null
        ));
    }
}
