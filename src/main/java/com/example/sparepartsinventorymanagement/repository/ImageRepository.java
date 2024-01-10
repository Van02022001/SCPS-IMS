package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.Image;
import com.example.sparepartsinventorymanagement.entities.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findBySubCategory(SubCategory subCategory);
}
