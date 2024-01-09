package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.SubCategory;
import com.example.sparepartsinventorymanagement.entities.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {
    Optional<Size> findBySubCategory(SubCategory subCategory);
}
