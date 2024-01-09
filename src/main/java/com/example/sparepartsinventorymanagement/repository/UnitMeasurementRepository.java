package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.UnitMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UnitMeasurementRepository extends JpaRepository<UnitMeasurement, Long> {
    boolean existsByName(String name);
    List<UnitMeasurement> findByNameContaining(String keyword);
}
