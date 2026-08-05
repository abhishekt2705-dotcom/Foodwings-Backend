package com.foodwings.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private String status;
    private Long customerId;
    private String customerName;
    private Long restaurantId;
    private String restaurantName;
    private String deliveryPartnerName;
    private String deliveryAddress;
    private List<OrderItemResponse> items;
    private PaymentResponse payment;
    private String couponCode;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal deliveryFee;
    private BigDecimal finalAmount;
    private LocalDateTime placedAt;
    private LocalDateTime updatedAt;
}
