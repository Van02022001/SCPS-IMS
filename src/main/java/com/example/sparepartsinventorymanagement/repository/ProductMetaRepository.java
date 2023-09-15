package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.Product;
import com.example.sparepartsinventorymanagement.entities.ProductMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductMetaRepository extends JpaRepository<ProductMeta, Long> {
    boolean existsByKey(String key);
    List<ProductMeta> findByProduct(Product product);
}
