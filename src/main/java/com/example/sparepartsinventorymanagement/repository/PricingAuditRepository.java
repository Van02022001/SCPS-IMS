package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.PricingAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PricingAuditRepository extends JpaRepository<PricingAudit, Long> {
    List<PricingAudit> findByPricingItemId(Long itemId);

    List<PricingAudit> findByPricingId(Long pricingId);

}
