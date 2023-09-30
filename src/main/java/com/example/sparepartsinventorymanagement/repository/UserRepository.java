package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.dto.request.UpdateUserForm;
import com.example.sparepartsinventorymanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    List<User>  findAll();


    User getUserById(Long id);

    int deleteUserById(Long id);



}
