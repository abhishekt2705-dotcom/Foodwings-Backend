package com.foodwings.dto.request;

import com.foodwings.enums.FoodType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
public class FoodRequest {

    @NotBlank(message = "Food name is required")
    @Size(max = 120)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "Discount cannot be negative")
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;

    @NotNull(message = "Food type is required")
    private FoodType foodType;

    @NotNull(message = "Category id is required")
    private Long categoryId;

    @NotNull(message = "Restaurant id is required")
    private Long restaurantId;

    @Builder.Default
    private boolean available = true;

    @Builder.Default
    private boolean bestSeller = false;

    @Builder.Default
    private boolean popular = false;
}
