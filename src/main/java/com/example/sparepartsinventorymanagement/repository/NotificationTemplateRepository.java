package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.NotificationTemplate;
import com.example.sparepartsinventorymanagement.entities.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    Optional<NotificationTemplate> findByType(NotificationType type);
}
