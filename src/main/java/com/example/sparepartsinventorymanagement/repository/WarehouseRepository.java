package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.User;
import com.example.sparepartsinventorymanagement.entities.Warehouse;
import com.example.sparepartsinventorymanagement.entities.WarehouseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    boolean existsByName(String name);
    boolean existsByAddress(String address);
    List<Warehouse> findByNameContaining(String keyword);
    List<Warehouse> findByStatus(WarehouseStatus status);


}
