package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.ReceiptDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptDetailRepository extends JpaRepository<ReceiptDetail, Long> {
}
