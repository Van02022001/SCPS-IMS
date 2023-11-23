package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.Item;
import com.example.sparepartsinventorymanagement.entities.Location;
import com.example.sparepartsinventorymanagement.entities.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByWarehouse(Warehouse warehouse);
    Optional<Location> findByIdAndWarehouse(Long id, Warehouse warehouse);
    Optional<Location> findByIdAndItem(Long id, Item item);
}
