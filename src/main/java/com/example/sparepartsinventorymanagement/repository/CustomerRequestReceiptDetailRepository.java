package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.CustomerRequestReceiptDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRequestReceiptDetailRepository extends JpaRepository<CustomerRequestReceiptDetail, Long> {
}
