package com.foodwings.service;

import com.foodwings.dto.response.NotificationResponse;
import com.foodwings.entity.User;
import com.foodwings.enums.NotificationType;

import java.util.List;

public interface NotificationService {

    void send(User user, NotificationType type, String title, String message);

    List<NotificationResponse> getMyNotifications(Long userId);

    long unreadCount(Long userId);

    void markAsRead(Long userId, Long notificationId);

    void markAllAsRead(Long userId);
}
