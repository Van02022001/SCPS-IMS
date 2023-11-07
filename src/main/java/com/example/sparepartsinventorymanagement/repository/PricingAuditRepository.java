package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.PricingAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PricingAuditRepository extends JpaRepository<PricingAudit, Long> {
}
