package com.example.sparepartsinventorymanagement.repository;


import com.example.sparepartsinventorymanagement.entities.InventoryDiscrepancyLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryDiscrepancyLogsRepository extends JpaRepository<InventoryDiscrepancyLogs, Long> {
}