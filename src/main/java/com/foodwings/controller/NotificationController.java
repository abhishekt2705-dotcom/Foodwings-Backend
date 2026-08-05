package com.foodwings.controller;

import com.foodwings.dto.response.NotificationResponse;
import com.foodwings.response.ApiResponse;
import com.foodwings.security.CurrentUserService;
import com.foodwings.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "In-app notifications for the current user")
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;

    public NotificationController(NotificationService notificationService, CurrentUserService currentUserService) {
        this.notificationService = notificationService;
        this.currentUserService = currentUserService;
    }

    private Long userId() {
        return currentUserService.getCurrentUserId();
    }

    @GetMapping
    @Operation(summary = "List the current user's notifications")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> list() {
        return ResponseEntity.ok(ApiResponse.success("Notifications fetched", notificationService.getMyNotifications(userId())));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Count unread notifications")
    public ResponseEntity<ApiResponse<Map<String, Long>>> unreadCount() {
        return ResponseEntity.ok(ApiResponse.success("Unread count",
                Map.of("unread", notificationService.unreadCount(userId()))));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<ApiResponse<Void>> markRead(@PathVariable Long id) {
        notificationService.markAsRead(userId(), id);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read"));
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<ApiResponse<Void>> markAllRead() {
        notificationService.markAllAsRead(userId());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read"));
    }
}
