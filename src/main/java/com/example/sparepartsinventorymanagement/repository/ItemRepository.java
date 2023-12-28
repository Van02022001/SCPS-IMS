package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findBySubCategory(SubCategory subCategory);
    List<Item> findByStatus(ItemStatus status);
    boolean existsBySubCategoryAndOriginAndBrandAndSupplier(SubCategory subCategory, Origin origin, Brand brand, Supplier supplier);
    List<Item> findBySubCategory_NameContainingIgnoreCase(String name);
    Boolean existsItemByCodeEqualsIgnoreCase(String code);


}

