package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.Pricing;
import com.example.sparepartsinventorymanagement.entities.PricingAudit;
import com.example.sparepartsinventorymanagement.entities.PurchasePriceAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PricingRepository extends JpaRepository<Pricing, Long> {

}
