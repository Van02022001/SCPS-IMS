package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.Location;
import com.example.sparepartsinventorymanagement.entities.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByWarehouseAndTagsIn(Warehouse warehouse, List<String> tags);
}
