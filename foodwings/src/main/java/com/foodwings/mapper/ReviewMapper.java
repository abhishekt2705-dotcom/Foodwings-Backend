package com.foodwings.mapper;

import com.foodwings.dto.response.ReviewResponse;
import com.foodwings.entity.Review;

/**
 * Maps {@link Review} entities to response DTOs.
 */
public final class ReviewMapper {

    private ReviewMapper() {
    }

    public static ReviewResponse toResponse(Review r) {
        return ReviewResponse.builder()
                .id(r.getId())
                .userId(r.getUser().getId())
                .userName(r.getUser().getName())
                .restaurantId(r.getRestaurant() != null ? r.getRestaurant().getId() : null)
                .foodItemId(r.getFoodItem() != null ? r.getFoodItem().getId() : null)
                .rating(r.getRating())
                .comment(r.getComment())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
