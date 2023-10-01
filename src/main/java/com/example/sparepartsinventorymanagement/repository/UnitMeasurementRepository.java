package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.UnitMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnitMeasurementRepository extends JpaRepository<UnitMeasurement, Long> {
    boolean existsByName(String name);
    List<UnitMeasurement> findByNameContaining(String keyword);
}
