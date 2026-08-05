package com.foodwings.service;

import com.foodwings.dto.request.AddToCartRequest;
import com.foodwings.dto.request.UpdateCartItemRequest;
import com.foodwings.dto.response.CartResponse;
import com.foodwings.entity.Cart;

public interface CartService {

    CartResponse getCart(Long userId);

    CartResponse addItem(Long userId, AddToCartRequest request);

    CartResponse updateItem(Long userId, UpdateCartItemRequest request);

    CartResponse removeItem(Long userId, Long foodItemId);

    CartResponse applyCoupon(Long userId, String code);

    CartResponse removeCoupon(Long userId);

    void clearCart(Long userId);

    /** Internal helper used by the order module to fetch the managed cart entity. */
    Cart getOrCreateCartEntity(Long userId);

    /** Recalculates totals / discount for the given cart and persists it. */
    void recalculate(Cart cart);
}
