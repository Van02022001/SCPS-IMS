package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.Item;
import com.example.sparepartsinventorymanagement.entities.PurchasePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchasePriceRepository extends JpaRepository<PurchasePrice, Long> {


    Optional<PurchasePrice> findByItemId(Long itemId);

    PurchasePrice findByItem(Item item);
    PurchasePrice findByItem_Id(Long itemId);
}
