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
public class EarningsResponse {
    private Long restaurantId;
    private String restaurantName;
    private long deliveredOrders;
    private BigDecimal totalEarnings;
}
