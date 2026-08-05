package com.foodwings.mapper;

import com.foodwings.dto.response.NotificationResponse;
import com.foodwings.entity.Notification;

/**
 * Maps {@link Notification} entities to response DTOs.
 */
public final class NotificationMapper {

    private NotificationMapper() {
    }

    public static NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType().name())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
