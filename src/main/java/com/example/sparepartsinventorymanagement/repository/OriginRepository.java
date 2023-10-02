package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.Origin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OriginRepository extends JpaRepository<Origin, Long> {
    List<Origin> findByNameContaining(String keyword);
    boolean existsByName(String name);
}
