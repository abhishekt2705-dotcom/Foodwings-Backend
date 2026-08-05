package com.foodwings.mapper;

import com.foodwings.dto.response.CartItemResponse;
import com.foodwings.dto.response.CartResponse;
import com.foodwings.entity.Cart;
import com.foodwings.entity.CartItem;

import java.math.BigDecimal;

/**
 * Maps {@link Cart} entities to response DTOs.
 */
public final class CartMapper {

    private CartMapper() {
    }

    public static CartItemResponse toItemResponse(CartItem item) {
        BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return CartItemResponse.builder()
                .id(item.getId())
                .foodItemId(item.getFoodItem().getId())
                .foodName(item.getFoodItem().getName())
                .imagePath(item.getFoodItem().getImagePath())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .subtotal(subtotal)
                .build();
    }

    public static CartResponse toResponse(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .items(cart.getItems().stream().map(CartMapper::toItemResponse).toList())
                .couponCode(cart.getCoupon() != null ? cart.getCoupon().getCode() : null)
                .totalAmount(cart.getTotalAmount())
                .discountAmount(cart.getDiscountAmount())
                .finalAmount(cart.getFinalAmount())
                .itemCount(cart.getItems().stream().mapToInt(CartItem::getQuantity).sum())
                .build();
    }
}
