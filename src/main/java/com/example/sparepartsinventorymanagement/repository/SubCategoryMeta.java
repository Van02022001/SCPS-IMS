package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubCategoryMeta extends JpaRepository<com.example.sparepartsinventorymanagement.entities.SubCategoryMeta, Long> {
    boolean existsByKeyAndSubCategory(String key, SubCategory subCategory);
    List<com.example.sparepartsinventorymanagement.entities.SubCategoryMeta> findBySubCategory(SubCategory subCategory);
}
