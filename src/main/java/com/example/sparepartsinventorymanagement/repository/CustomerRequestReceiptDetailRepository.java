package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.CustomerRequestReceipt;
import com.example.sparepartsinventorymanagement.entities.CustomerRequestReceiptDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRequestReceiptDetailRepository extends JpaRepository<CustomerRequestReceiptDetail, Long> {
    List<CustomerRequestReceiptDetail> findByCustomerRequestReceiptId(Long receiptId);
}
