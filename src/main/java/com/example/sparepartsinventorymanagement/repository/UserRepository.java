package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.dto.request.LoginForm;
import com.example.sparepartsinventorymanagement.dto.request.LogoutForm;
import com.example.sparepartsinventorymanagement.dto.request.RefreshTokenRequest;
import com.example.sparepartsinventorymanagement.dto.request.UpdateUserForm;
import com.example.sparepartsinventorymanagement.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    List<User>  findAll();


    User getUserById(Long id);

    int deleteUserById(Long id);


    Optional<User> findByUsername(String username);
}
