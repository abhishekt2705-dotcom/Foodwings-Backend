package com.foodwings.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddToCartRequest {

    @NotNull(message = "Food item id is required")
    private Long foodItemId;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Positive
    @Builder.Default
    private int quantity = 1;
}
