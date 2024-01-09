package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByTaxCode(String taxCode);

    void deleteById(Long id);
    Optional<Supplier> findByIdAndStatus(Long id, boolean status);
}
