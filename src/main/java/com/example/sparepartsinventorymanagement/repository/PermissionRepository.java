package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
