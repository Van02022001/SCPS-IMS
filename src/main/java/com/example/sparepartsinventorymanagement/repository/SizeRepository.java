package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.Size;
import com.example.sparepartsinventorymanagement.entities.UnitMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SizeRepository extends JpaRepository<Size, Long> {
    boolean existsByLengthAndWidthAndHeightAndUnitMeasurement(float length, float width, float height, UnitMeasurement unitMeasurement);
}
