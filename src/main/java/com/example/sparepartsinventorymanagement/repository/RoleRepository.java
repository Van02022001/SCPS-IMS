package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
