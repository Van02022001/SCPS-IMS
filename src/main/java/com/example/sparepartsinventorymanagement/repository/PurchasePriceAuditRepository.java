package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.PurchasePriceAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchasePriceAuditRepository extends JpaRepository<PurchasePriceAudit, Long> {
}
