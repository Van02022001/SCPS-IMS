package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.PasswordResetToken;
import com.example.sparepartsinventorymanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
    Optional<PasswordResetToken> findByUser(User user);

}
