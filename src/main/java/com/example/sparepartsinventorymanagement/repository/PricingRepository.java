package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PricingRepository extends JpaRepository<Pricing, Long> {
    Pricing findByItem(Item item);
}
