package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.InventoryDiscrepancyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryDiscrepancyLogRepository extends JpaRepository<InventoryDiscrepancyLog, Long> {
}
