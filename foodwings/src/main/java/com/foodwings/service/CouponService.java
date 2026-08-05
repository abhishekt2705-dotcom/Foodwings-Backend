package com.foodwings.service;

import com.foodwings.dto.request.CouponRequest;
import com.foodwings.dto.response.CouponResponse;
import com.foodwings.entity.Coupon;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService {

    CouponResponse create(CouponRequest request);

    List<CouponResponse> getAll();

    CouponResponse getById(Long id);

    void delete(Long id);

    /**
     * Validates a coupon code against the given order amount and returns the entity.
     */
    Coupon validate(String code, BigDecimal orderAmount);

    /**
     * Computes the discount a coupon grants for the given order amount, capped at maxDiscount.
     */
    BigDecimal computeDiscount(Coupon coupon, BigDecimal orderAmount);
}
