package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.SubCategory;
import com.example.sparepartsinventorymanagement.entities.SubCategoryMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubCategoryMetaRepository extends JpaRepository<SubCategoryMeta, Long> {

    Optional<SubCategoryMeta> findBySubCategory(SubCategory subCategory);
}
