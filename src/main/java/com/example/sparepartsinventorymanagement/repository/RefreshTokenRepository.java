package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Query("select r from RefreshToken r where r.user.id = ?1")
    Optional<RefreshToken> findByUser_Id(Long userId);

    @Query("delete from RefreshToken r where r.user.id = ?1")
    @Modifying
    @Transactional
    void deleteAllByUser_Id(Long id);

    @Transactional
    @Modifying
    @Query("delete from RefreshToken r where r.token = ?1")
    void deleteByToken(String token);

    @Transactional
    @Modifying
    @Query("delete from RefreshToken r where r.expiryDate <= ?1")
    void deleteAllByExpiryDateBefore(Instant now);
}
