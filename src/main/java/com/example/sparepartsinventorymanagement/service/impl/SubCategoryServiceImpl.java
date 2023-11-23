package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.SubCategoryFormRequest;
import com.example.sparepartsinventorymanagement.dto.response.SubCategoryDTO;
import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.exception.InvalidResourceException;
import com.example.sparepartsinventorymanagement.exception.InvalidStatusException;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.*;
import com.example.sparepartsinventorymanagement.service.SubCategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubCategoryServiceImpl implements SubCategoryService {

    private final UnitRepository unitRepository;
    private final SizeRepository sizeRepository;
    private final UnitMeasurementRepository unitMeasurementRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper mapper;
    private final ItemRepository itemRepository;
    @Override
    public List<SubCategoryDTO> getAll() {
        List<SubCategory> subCategories = subCategoryRepository.findAll();
        return mapper.map(subCategories, new TypeToken<List<SubCategoryDTO>>() {
        }.getType());
    }

    @Override
    public SubCategoryDTO getSubCategoryById(Long id) {
        SubCategory subCategory = subCategoryRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Product not found")
        );
        return mapper.map(subCategory, SubCategoryDTO.class);
    }

    @Override
    public List<SubCategoryDTO> findByName(String name) {
        List<SubCategory> subCategories = subCategoryRepository.findByNameContaining(name);
        return mapper.map(subCategories, new TypeToken<List<SubCategoryDTO>>() {
        }.getType());
    }

    @Override
    public List<SubCategoryDTO> getActiveSubCategories() {
        List<SubCategory> subCategories = subCategoryRepository.findByStatus(SubCategoryStatus.Active);
        return mapper.map(subCategories, new TypeToken<List<SubCategoryDTO>>() {
        }.getType());
    }

    @Override
    public List<SubCategoryDTO> getSubCategoriesByCategory(Set<Long> ids) {
        List<Category> categories = new ArrayList<>();
        for (Long id: ids
             ) {
            Category category = categoryRepository.findById(id).orElseThrow(
                    ()-> new NotFoundException("Category not found")
            );
            categories.add(category);
        }

        List<SubCategory> subCategories = subCategoryRepository.findByCategoriesIn(categories);
        return mapper.map(subCategories, new TypeToken<List<SubCategoryDTO>>() {
        }.getType());
    }

    @Override
    public SubCategoryDTO createSubCategory(SubCategoryFormRequest form) {

        Set<Category> categories = new HashSet<>();
        for (Long id : form.getCategories_id()
             ) {
            Category category = categoryRepository.findById(id).orElseThrow(
                    ()-> new NotFoundException("Category not found")
            );
            if(category.getStatus() == CategoryStatus.Inactive){
                throw new InvalidStatusException(category.getName(), " is invalid by status");
            }
           categories.add(category);
        }
        if(categories.isEmpty()){
            throw new InvalidResourceException("SubCategory must have at least one category");
        }

        checkNameDuplicate(form.getName().trim());
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
        return mapper.map(subCategory, SubCategoryDTO.class);
    }

    @Override
    public SubCategoryDTO updateSubCategory(Long id, SubCategoryFormRequest form) {
        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        // Check and update name
        if (!subCategory.getName().equalsIgnoreCase(form.getName())) {
            checkNameDuplicate(form.getName().trim());
            subCategory.setName(form.getName());
            if(!subCategory.getItems().isEmpty()){
                for (Item item: subCategory.getItems()
                ) {
                    int v1 = item.getCode().indexOf("-");
                    StringBuilder result = new StringBuilder();
                    String[] ws = subCategory.getName().split(" ");
                    for (String word : ws) {
                        if (!word.isEmpty()) {
                            result.append(word.substring(0, 1).toUpperCase());
                        }
                    }

                    String code = result +"-"+ item.getCode().substring(v1+1);
                    if(!item.getCode().substring(0, v1).equalsIgnoreCase(result.toString())){
                        String newCode = code;
                        int count = 0;
                        while (itemRepository.existsItemByCodeEqualsIgnoreCase(newCode)){
                            count++;
                            newCode = code + "-N" + count;
                        }
                        item.setCode(newCode);
                        itemRepository.save(item);
                    }
                }
            }
        }

        // Check and update description
        if (!Objects.equals(subCategory.getDescription(), form.getDescription())) {
            subCategory.setDescription(form.getDescription());
        }
        // Check if there are any inactive categories in the input list
        if (form.getCategories_id().stream()
                .anyMatch(ct_di -> {
                    Category category = categoryRepository.findById(ct_di)
                            .orElseThrow(() -> new NotFoundException("Category not found"));
                    return category.getStatus() == CategoryStatus.Inactive;
                })) {
            throw new InvalidResourceException("One or more categories are invalid or inactive");
        }
        // Check and update categories
        Set<Category> updatedCategories = form.getCategories_id().stream()
                .map(ct_di -> categoryRepository.findById(ct_di)
                        .orElseThrow(() -> new NotFoundException("Category not found")))
                .collect(Collectors.toSet());

        if (updatedCategories.isEmpty()) {
            throw new InvalidResourceException("SubCategory must have at least one category");
        }
        subCategory.setCategories(updatedCategories);

        // Check and update size
        Size size = sizeRepository.findBySubCategory(subCategory)
                .orElseThrow(() -> new NotFoundException("Size of SubCategory not found"));

        if (size.getHeight() != form.getHeight() ||
                size.getWidth() != form.getWidth() ||
                size.getLength() != form.getLength() ||
                size.getDiameter() != form.getDiameter() ||
                !Objects.equals(size.getUnitMeasurement(), unitMeasurementRepository.findById(form.getUnit_mea_id())
                        .orElseThrow(() -> new NotFoundException("Unit of measurement not found")))) {
            size.setHeight(form.getHeight());
            size.setWidth(form.getWidth());
            size.setLength(form.getLength());
            size.setDiameter(form.getDiameter());
            size.setUnitMeasurement(unitMeasurementRepository.findById(form.getUnit_mea_id())
                    .orElseThrow(() -> new NotFoundException("Unit of measurement not found")));

            if(!subCategory.getItems().isEmpty()){
                for (Item item: subCategory.getItems()
                ) {
                    int v1 = item.getCode().indexOf("-");
                    int v2 = item.getCode().indexOf("-", v1 +  1);
                    int v3 = item.getCode().indexOf("-", v2 +  1);
                    int v4 = item.getCode().indexOf("-", v3 +  1);
                    String newStr = size.getLength() +"x"+size.getWidth()
                            +"x"+size.getHeight()+ "-" +size.getDiameter();
                    String code = item.getCode().substring(0,v4) + "-" + newStr;
                    if(!item.getCode().substring(v4).equalsIgnoreCase(newStr)){
                        String newCode = code;
                        int count = 0;
                        while (itemRepository.existsItemByCodeEqualsIgnoreCase(newCode)){
                            count++;
                            newCode = code + "-N" + count;
                        }
                        item.setCode(newCode);
                        itemRepository.save(item);
                    }
                }
            }
        }

        // Check and update unit
        Unit unit = unitRepository.findById(form.getUnit_id())
                .orElseThrow(() -> new NotFoundException("Unit not found"));
        subCategory.setUnit(unit);

        // Update the last modified timestamp
        Date currentDate = new Date();
        subCategory.setUpdatedAt(currentDate);

        // Save the changes
        sizeRepository.save(size);
        subCategoryRepository.save(subCategory);

        return mapper.map(subCategory, SubCategoryDTO.class);
    }

    @Override
    public SubCategoryDTO updateSubCategoryStatus(Long id, SubCategoryStatus status) {
        SubCategory subCategory = subCategoryRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Sub category not found")
        );
        if(status == SubCategoryStatus.Active){
            subCategory.setStatus(SubCategoryStatus.Active);
        }else {
            subCategory.setStatus(SubCategoryStatus.Inactive);
        }
        subCategoryRepository.save(subCategory);
        return mapper.map(subCategory, SubCategoryDTO.class);
    }
    private void checkNameDuplicate(String name){
        List<SubCategory> lists = subCategoryRepository.findAll();
        if(lists.stream().anyMatch(subCategory -> subCategory.getName().equalsIgnoreCase(name.trim()))){
            throw new InvalidResourceException("Sub category name was existed");
        }
    }
}
