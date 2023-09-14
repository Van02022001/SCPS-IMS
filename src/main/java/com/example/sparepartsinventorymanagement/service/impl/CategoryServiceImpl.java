package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.CreateCategoryForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateCategoryForm;
import com.example.sparepartsinventorymanagement.entities.Category;
import com.example.sparepartsinventorymanagement.entities.CategoryStatus;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.CategoryRepository;
import com.example.sparepartsinventorymanagement.service.CategoryService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public ResponseEntity<ResponseObject> getAll() {
        List<Category> categories = categoryRepository.findAll();
        if(categories.size() > 0){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(),"Get list categories successfully.", categories
            ));

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                HttpStatus.NOT_FOUND.toString(),"List empty", null
        ));
    }

    @Override
    public ResponseEntity<ResponseObject> getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Category not found.")
        );
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Get category by id successfully", category
        ));
    }

    @Override
    public ResponseEntity searchCategoryByName(String name) {
        List<Category> categories = categoryRepository.findByNameContaining(name);
        if(categories.size() > 0){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(), "Search category by name successfully", categories
            ));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                HttpStatus.NOT_FOUND.toString(), "List is empty",null
        ));
    }

    @Override
    public ResponseEntity createCategory(CreateCategoryForm form) {
        if(categoryRepository.existsByName(form.getName())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(), "Name of category was existed", null
            ));
        }
        ModelMapper mapper = new ModelMapper();
        Category category = mapper.map(form, Category.class);
        Date currentDate = new Date();
        category.setUpdatedAt(currentDate);
        category.setCreatedAt(currentDate);
        category.setStatus(CategoryStatus.Active);
        categoryRepository.save(category);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Create category successfully.", category
        ));
    }


    @Override
    public ResponseEntity updateCategory(Long id, UpdateCategoryForm form) {
        Category category = categoryRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Category not found")
        );
        category.setName(form.getName());
        category.setDescription(form.getDescription());
        Date currentDate = new Date();
        category.setUpdatedAt(currentDate);
        categoryRepository.save(category);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Update category successfully", category
        ));
    }

    @Override
    public ResponseEntity updateCategoryStatus(Long id, CategoryStatus status) {
        Category category = categoryRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Category not found")
        );
        if(status == CategoryStatus.Inactive){
            category.setStatus(CategoryStatus.Inactive);
        }else{
            category.setStatus(CategoryStatus.Active);
        }
        categoryRepository.save(category);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Update category status successfully", null
        ));
    }

    @Override
    public ResponseEntity getActiveCategories() {
        List<Category> categories = categoryRepository.findByStatus(CategoryStatus.Active);

        if(categories.size() > 0){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(),"Get list categories successfully.", categories
            ));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                HttpStatus.NOT_FOUND.toString(),"List empty", null
        ));
    }
}
