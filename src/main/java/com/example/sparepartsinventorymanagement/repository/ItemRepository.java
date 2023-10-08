package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.Item;
import com.example.sparepartsinventorymanagement.entities.ItemStatus;
import com.example.sparepartsinventorymanagement.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByProduct(Product product);
    List<Item> findByStatus(ItemStatus status);
}
