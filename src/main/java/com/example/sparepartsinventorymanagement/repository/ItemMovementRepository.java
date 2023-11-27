package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.Item;
import com.example.sparepartsinventorymanagement.entities.ItemMovement;
import com.example.sparepartsinventorymanagement.entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemMovementRepository extends JpaRepository<ItemMovement, Long> {
    List<ItemMovement> findByItem(Item item);
}
