package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.CustomerRequestReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRequestReceiptRepository extends JpaRepository<CustomerRequestReceipt, Long> {

    List<CustomerRequestReceipt> findByWarehouseId(Long warehouseId);
}
