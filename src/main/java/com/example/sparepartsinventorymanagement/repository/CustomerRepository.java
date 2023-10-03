package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    int deleteCustomerById(Long id);
}
