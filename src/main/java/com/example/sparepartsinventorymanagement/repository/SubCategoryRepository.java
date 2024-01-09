package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.Category;
import com.example.sparepartsinventorymanagement.entities.SubCategory;
import com.example.sparepartsinventorymanagement.entities.SubCategoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    boolean existsByName(String name);
    Optional<SubCategory> findByIdAndStatus(Long id, SubCategoryStatus status);
    List<SubCategory> findByNameContaining(String name);
    List<SubCategory> findByStatus(SubCategoryStatus status);
    List<SubCategory> findByCategoriesIn(List<Category> categories);
}
