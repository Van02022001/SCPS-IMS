package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.ItemMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemMovementRepository extends JpaRepository<ItemMovement, Long> {
}
