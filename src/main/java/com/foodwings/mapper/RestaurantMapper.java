package com.foodwings.mapper;

import com.foodwings.dto.response.RestaurantResponse;
import com.foodwings.entity.Restaurant;

/**
 * Maps {@link Restaurant} entities to response DTOs.
 */
public final class RestaurantMapper {

    private RestaurantMapper() {
    }

    public static RestaurantResponse toResponse(Restaurant r) {
        return RestaurantResponse.builder()
                .id(r.getId())
                .name(r.getName())
                .description(r.getDescription())
                .address(r.getAddress())
                .city(r.getCity())
                .phone(r.getPhone())
                .email(r.getEmail())
                .logo(r.getLogo())
                .banner(r.getBanner())
                .openingTime(r.getOpeningTime())
                .closingTime(r.getClosingTime())
                .status(r.getStatus().name())
                .active(r.isActive())
                .rating(r.getRating())
                .totalReviews(r.getTotalReviews())
                .ownerId(r.getOwner() != null ? r.getOwner().getId() : null)
                .ownerName(r.getOwner() != null ? r.getOwner().getName() : null)
                .build();
    }
}
