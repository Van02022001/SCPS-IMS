package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.Notification;
import com.example.sparepartsinventorymanagement.entities.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Tìm tất cả thông báo dựa trên ID người dùng
    List<Notification> findByUserId(Long userId);

    // Tìm thông báo dựa trên ID người dùng và trạng thái đã đọc
    List<Notification> findByUserIdAndSeen(Long userId, Boolean seen);

    // Tìm thông báo dựa trên ID người dùng, trạng thái đã đọc và loại thông báo
    List<Notification> findByUserIdAndSeenAndType(Long userId, Optional<Boolean> seen, NotificationType type);

    // Tìm thông báo dựa trên ID người dùng và loại thông báo
    List<Notification> findByUserIdAndType(Long userId, NotificationType type);
}
