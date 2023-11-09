package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.CustomerRequestReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRequestReceiptRepository extends JpaRepository<CustomerRequestReceipt, Long> {
}
