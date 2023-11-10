package com.example.sparepartsinventorymanagement.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TokenCleanupTask {

    @PersistenceContext
    private EntityManager entityManager;

    @Scheduled(cron = "0 0 * * * *") // Ví dụ: Chạy hàng giờ
    @Transactional
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();
        entityManager.createQuery("DELETE FROM RefreshToken t WHERE t.expiryDate < :now")
                .setParameter("now", now)
                .executeUpdate();
    }
}