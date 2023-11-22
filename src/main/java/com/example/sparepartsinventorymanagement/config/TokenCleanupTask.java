package com.example.sparepartsinventorymanagement.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@EnableScheduling
public class TokenCleanupTask {

    @PersistenceContext
    private EntityManager entityManager;

    @Scheduled(cron = "0 0 * * * *") // Chạy hàng giờ
    @Transactional
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();
        int deletedCount = entityManager.createQuery("DELETE FROM RefreshToken t WHERE t.expiryDate < :now")
                .setParameter("now", now)
                .executeUpdate();
        entityManager.flush(); // Đảm bảo changes được flush sau khi execute update

        // Logging for debugging
        System.out.println("Deleted " + deletedCount + " expired refresh tokens at " + now);
    }
}