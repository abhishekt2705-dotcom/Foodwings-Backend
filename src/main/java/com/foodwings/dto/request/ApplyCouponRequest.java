package com.foodwings.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class ApplyCouponRequest {

    @NotBlank(message = "Coupon code is required")
    private String code;

    // Explicit getter for IDEs that may not recognise Lombok-generated methods
    public String getCode() {
        return this.code;
    }
}
