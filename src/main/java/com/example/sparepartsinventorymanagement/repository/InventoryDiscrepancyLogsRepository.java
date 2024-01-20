package com.example.sparepartsinventorymanagement.repository;


import com.example.sparepartsinventorymanagement.entities.InventoryDiscrepancyLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryDiscrepancyLogsRepository extends JpaRepository<InventoryDiscrepancyLogs, Long> {
    @Query("SELECT idl FROM InventoryDiscrepancyLogs idl WHERE idl.inventory.item.id = :itemId AND idl.inventory.warehouse.id = :warehouseId ORDER BY idl.logTime DESC")
    Optional<InventoryDiscrepancyLogs> findTopByItemIdAndWarehouseIdOrderByLogTimeDesc(@Param("itemId") Long itemId, @Param("warehouseId") Long warehouseId);



}