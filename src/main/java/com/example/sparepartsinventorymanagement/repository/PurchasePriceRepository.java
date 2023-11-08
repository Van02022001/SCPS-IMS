package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.PurchasePrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchasePriceRepository extends JpaRepository<PurchasePrice, Long> {
}
