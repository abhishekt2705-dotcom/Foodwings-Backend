package com.foodwings.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discount;
    private BigDecimal effectivePrice;
    private String foodType;
    private String imagePath;
    private boolean available;
    private boolean bestSeller;
    private boolean popular;
    private double rating;
    private Long categoryId;
    private String categoryName;
    private Long restaurantId;
    private String restaurantName;
}
