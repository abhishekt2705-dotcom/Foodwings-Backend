package com.foodwings.mapper;

import com.foodwings.dto.response.CouponResponse;
import com.foodwings.entity.Coupon;

/**
 * Maps {@link Coupon} entities to response DTOs.
 */
public final class CouponMapper {

    private CouponMapper() {
    }

    public static CouponResponse toResponse(Coupon c) {
        return CouponResponse.builder()
                .id(c.getId())
                .code(c.getCode())
                .description(c.getDescription())
                .discountPercentage(c.getDiscountPercentage())
                .minOrderAmount(c.getMinOrderAmount())
                .maxDiscount(c.getMaxDiscount())
                .expiryDate(c.getExpiryDate())
                .active(c.isActive())
                .build();
    }
}
