package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.SubCategory;
import com.example.sparepartsinventorymanagement.entities.SubCategoryMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductMetaRepository extends JpaRepository<SubCategoryMeta, Long> {
    boolean existsByKeyAndProduct(String key, SubCategory subCategory);
    List<SubCategoryMeta> findByProduct(SubCategory subCategory);
}
