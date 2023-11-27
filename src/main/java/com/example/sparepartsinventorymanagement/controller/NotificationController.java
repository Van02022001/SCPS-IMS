package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.response.NotificationDTO;
import com.example.sparepartsinventorymanagement.entities.Notification;
import com.example.sparepartsinventorymanagement.entities.NotificationType;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.service.NotificationService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "notification")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "Mark a notification as read")
    @PutMapping("/mark-as-read/{notificationId}")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable Long notificationId) {
        try {
            notificationService.markNotificationAsRead(notificationId);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Notification marked as read successfully",
                    null
            ));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    e.getMessage(),
                    null
            ));
        }
    }
    @Operation(summary = "Get all notifications for a user")
    @GetMapping("/user-notifications/{userId}")
    public ResponseEntity<?> getAllUserNotifications(@PathVariable Long userId) {
        try {
            List<NotificationDTO> notifications = notificationService.getAllNotifications(userId);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Notifications retrieved successfully",
                    notifications
            ));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    e.getMessage(),
                    null
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                    "An error occurred while retrieving notifications",
                    null
            ));
        }
    }

    @Operation(summary = "Get notifications for a user")
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getNotificationsForUser(
            @PathVariable Long userId,
            @RequestParam Optional<Boolean> isRead,
            @RequestParam Optional<NotificationType> type) {
        List<NotificationDTO> notifications = notificationService.getNotificationsForUser(userId, isRead, type);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.toString(),
                "Notifications retrieved successfully",
                notifications
        ));
    }

    @Operation(summary = "Get notifications detail for a user")
    @GetMapping("/users/{notificationId}")
    public ResponseEntity<?> getNotificationsForUser(
            @PathVariable Long notificationId
            ) {
        try {
        NotificationDTO notifications = notificationService.getNotificationDetails(notificationId);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.toString(),
                "Notifications retrieved successfully",
                notifications
        ));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    e.getMessage(),
                    null
            ));
        }
    }
    @Operation(summary = "Delete a notification")
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long notificationId) {
        try {
            notificationService.deleteNotification(notificationId);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Notification deleted successfully",
                    null
            ));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    e.getMessage(),
                    null
            ));
        }
    }

    @Operation(summary = "Mark all notifications as read for a user")
    @PutMapping("/mark-all-as-read/user/{userId}")
    public ResponseEntity<?> markAllNotificationsAsReadForUser(@PathVariable Long userId) {
        notificationService.markAllNotificationsAsReadForUser(userId);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.toString(),
                "All notifications marked as read successfully",
                null
        ));
    }

    @Operation(summary = "Restore a notification from trash")
    @PutMapping("/restore-from-trash/{notificationId}")
    public ResponseEntity<?> restoreNotificationFromTrash(@PathVariable Long notificationId) {
        try {
            notificationService.restoreNotificationFromTrash(notificationId);
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Notification restored from trash successfully",
                    null
            ));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    e.getMessage(),
                    null
            ));
        }
    }
}
