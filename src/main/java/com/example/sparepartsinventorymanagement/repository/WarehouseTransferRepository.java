package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.WarehouseTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseTransferRepository extends JpaRepository<WarehouseTransfer, Long> {
}
