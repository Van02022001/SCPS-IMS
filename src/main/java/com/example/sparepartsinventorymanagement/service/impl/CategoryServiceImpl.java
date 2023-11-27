package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.CategoryFormRequest;
import com.example.sparepartsinventorymanagement.dto.response.GetCategoryDTO;
import com.example.sparepartsinventorymanagement.entities.Category;
import com.example.sparepartsinventorymanagement.entities.CategoryStatus;
import com.example.sparepartsinventorymanagement.entities.SubCategory;
import com.example.sparepartsinventorymanagement.entities.SubCategoryStatus;
import com.example.sparepartsinventorymanagement.exception.InvalidResourceException;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.CategoryRepository;
import com.example.sparepartsinventorymanagement.repository.SubCategoryRepository;
import com.example.sparepartsinventorymanagement.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    private final SubCategoryRepository subCategoryRepository;
    private final ModelMapper mapper;
    @Override
    public List<GetCategoryDTO> getAll() {
        List<Category> res = categoryRepository.findAll();
        return mapper.map(res, new TypeToken<List<GetCategoryDTO>>(){}.getType());
    }

    @Override
    public GetCategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Category not found.")
        );
        return mapper.map(category, GetCategoryDTO.class);
    }

    @Override
    public List<GetCategoryDTO> searchCategoryByName(String name) {
        List<Category> categories = categoryRepository.findByNameContaining(name.trim());
        return mapper.map(categories, new TypeToken<List<GetCategoryDTO>>(){}.getType());
    }

    @Override
    public GetCategoryDTO createCategory(CategoryFormRequest form) {
        checkNameDuplicate(form.getName().trim());
        Date currentDate = new Date();
        Category category = Category.builder()
                .name(form.getName())
                .description(form.getDescription())
                .createdAt(currentDate)
                .updatedAt(currentDate)
                .status(CategoryStatus.Active)
                .build();
        categoryRepository.save(category);
        return mapper.map(category, GetCategoryDTO.class);
    }


    @Override
    public GetCategoryDTO updateCategory(Long id, CategoryFormRequest form) {
        Category category = categoryRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Category not found")
        );
        if(!category.getName().equalsIgnoreCase(form.getName().trim())){
            checkNameDuplicate(form.getName().trim());
            category.setName(form.getName());
        }
        category.setDescription(form.getDescription());
        Date currentDate = new Date();
        category.setUpdatedAt(currentDate);
        categoryRepository.save(category);
        return mapper.map(category, GetCategoryDTO.class);
    }

    @Override
    public GetCategoryDTO updateCategoryStatus(Long id, CategoryStatus status) {
        Category category = categoryRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Category not found")
        );
        if(status == CategoryStatus.Inactive){
            category.setStatus(CategoryStatus.Inactive);
            if(!category.getSubCategories().isEmpty()){
                for (SubCategory subCategory : category.getSubCategories()
                ) {
                    if(subCategory.getCategories().size() == 1){
                        subCategory.setStatus(SubCategoryStatus.Inactive);
                    }else{
                        subCategory.getCategories().remove(category);
                    }
                    subCategoryRepository.save(subCategory);
                }
            }
        }else{
            category.setStatus(CategoryStatus.Active);
            if(!category.getSubCategories().isEmpty()){
                for (SubCategory subCategory : category.getSubCategories()
                ) {
                    subCategory.setStatus(SubCategoryStatus.Active);
                    subCategoryRepository.save(subCategory);
                }
            }
        }
        categoryRepository.save(category);
        return mapper.map(category, GetCategoryDTO.class);
    }

    @Override
    public List<GetCategoryDTO> getActiveCategories() {
        List<Category> categories = categoryRepository.findByStatus(CategoryStatus.Active);
        return mapper.map(categories, new TypeToken<List<GetCategoryDTO>>(){}.getType());
    }

    private void checkNameDuplicate(String name){
        List<Category> categories = categoryRepository.findAll();
        if(categories.stream().anyMatch(category -> category.getName().equalsIgnoreCase(name.trim()))){
            throw new InvalidResourceException("Category name was existed");
        }
    }
}
