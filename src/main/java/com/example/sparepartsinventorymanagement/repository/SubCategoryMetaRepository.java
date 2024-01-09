package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.SubCategory;
import com.example.sparepartsinventorymanagement.entities.SubCategoryMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface SubCategoryMetaRepository extends JpaRepository<SubCategoryMeta, Long> {

    Optional<SubCategoryMeta> findBySubCategory(SubCategory subCategory);
}
