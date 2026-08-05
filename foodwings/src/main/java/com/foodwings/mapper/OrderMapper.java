package com.foodwings.mapper;

import com.foodwings.dto.response.OrderItemResponse;
import com.foodwings.dto.response.OrderResponse;
import com.foodwings.dto.response.PaymentResponse;
import com.foodwings.entity.Order;
import com.foodwings.entity.OrderItem;
import com.foodwings.entity.Payment;

/**
 * Maps {@link Order} entities to response DTOs.
 */
public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .foodItemId(item.getFoodItem() != null ? item.getFoodItem().getId() : null)
                .foodName(item.getFoodName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .subtotal(item.getSubtotal())
                .build();
    }

    public static PaymentResponse toPaymentResponse(Payment p) {
        if (p == null) {
            return null;
        }
        return PaymentResponse.builder()
                .id(p.getId())
                .method(p.getMethod().name())
                .status(p.getStatus().name())
                .amount(p.getAmount())
                .transactionId(p.getTransactionId())
                .paidAt(p.getPaidAt())
                .build();
    }

    public static OrderResponse toResponse(Order o) {
        return OrderResponse.builder()
                .id(o.getId())
                .orderNumber(o.getOrderNumber())
                .status(o.getStatus().name())
                .customerId(o.getCustomer().getId())
                .customerName(o.getCustomer().getName())
                .restaurantId(o.getRestaurant().getId())
                .restaurantName(o.getRestaurant().getName())
                .deliveryPartnerName(o.getDeliveryPartner() != null ? o.getDeliveryPartner().getName() : null)
                .deliveryAddress(o.getDeliveryAddressSnapshot())
                .items(o.getItems().stream().map(OrderMapper::toItemResponse).toList())
                .payment(toPaymentResponse(o.getPayment()))
                .couponCode(o.getCoupon() != null ? o.getCoupon().getCode() : null)
                .totalAmount(o.getTotalAmount())
                .discountAmount(o.getDiscountAmount())
                .deliveryFee(o.getDeliveryFee())
                .finalAmount(o.getFinalAmount())
                .placedAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .build();
    }
}
