package com.foodwings.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponResponse {
    private Long id;
    private String code;
    private String description;
    private BigDecimal discountPercentage;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscount;
    private LocalDate expiryDate;
    private boolean active;
}
