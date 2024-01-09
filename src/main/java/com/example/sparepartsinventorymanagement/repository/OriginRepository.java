package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.Origin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface OriginRepository extends JpaRepository<Origin, Long> {
    List<Origin> findByNameContaining(String keyword);
    boolean existsByName(String name);
}
