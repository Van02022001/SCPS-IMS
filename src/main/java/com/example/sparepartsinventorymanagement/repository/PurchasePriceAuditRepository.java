package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.PurchasePriceAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchasePriceAuditRepository extends JpaRepository<PurchasePriceAudit, Long> {





    List<PurchasePriceAudit> findByPurchasePriceId(Long purchasePriceId);

}

