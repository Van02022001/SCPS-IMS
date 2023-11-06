package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.Item;
import com.example.sparepartsinventorymanagement.entities.ItemStatus;
import com.example.sparepartsinventorymanagement.entities.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findBySubCategory(SubCategory subCategory);
    List<Item> findByStatus(ItemStatus status);
}
