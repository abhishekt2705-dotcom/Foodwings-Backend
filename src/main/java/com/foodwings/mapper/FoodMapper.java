package com.foodwings.mapper;

import com.foodwings.dto.response.FoodResponse;
import com.foodwings.entity.FoodItem;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Maps {@link FoodItem} entities to response DTOs, computing the discounted price.
 */
public final class FoodMapper {

    private FoodMapper() {
    }

    public static BigDecimal effectivePrice(FoodItem f) {
        BigDecimal discount = f.getDiscount() == null ? BigDecimal.ZERO : f.getDiscount();
        BigDecimal factor = BigDecimal.ONE.subtract(discount.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        return f.getPrice().multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }

    public static FoodResponse toResponse(FoodItem f) {
        return FoodResponse.builder()
                .id(f.getId())
                .name(f.getName())
                .description(f.getDescription())
                .price(f.getPrice())
                .discount(f.getDiscount())
                .effectivePrice(effectivePrice(f))
                .foodType(f.getFoodType().name())
                .imagePath(f.getImagePath())
                .available(f.isAvailable())
                .bestSeller(f.isBestSeller())
                .popular(f.isPopular())
                .rating(f.getRating())
                .categoryId(f.getCategory() != null ? f.getCategory().getId() : null)
                .categoryName(f.getCategory() != null ? f.getCategory().getName() : null)
                .restaurantId(f.getRestaurant() != null ? f.getRestaurant().getId() : null)
                .restaurantName(f.getRestaurant() != null ? f.getRestaurant().getName() : null)
                .build();
    }
}
