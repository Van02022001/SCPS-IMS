package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.Item;
import com.example.sparepartsinventorymanagement.entities.ItemMovement;
import com.example.sparepartsinventorymanagement.entities.Location;
import com.example.sparepartsinventorymanagement.entities.ReceiptDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemMovementRepository extends JpaRepository<ItemMovement, Long> {
    List<ItemMovement> findByItem(Item item);
    boolean existsByReceiptDetailAndToLocation(ReceiptDetail receiptDetail, Location location);
    boolean existsByReceiptDetailAndFromLocation(ReceiptDetail receiptDetail, Location location);
    boolean existsByReceiptDetail(ReceiptDetail receiptDetail);
}
