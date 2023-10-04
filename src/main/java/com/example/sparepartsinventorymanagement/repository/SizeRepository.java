package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.Product;
import com.example.sparepartsinventorymanagement.entities.Size;
import com.example.sparepartsinventorymanagement.entities.UnitMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SizeRepository extends JpaRepository<Size, Long> {
    Optional<Size> findByProduct(Product product);
}
