package com.foodwings.service;

import com.foodwings.dto.request.PlaceOrderRequest;
import com.foodwings.dto.response.OrderResponse;
import com.foodwings.enums.OrderStatus;
import com.foodwings.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse placeOrder(Long userId, PlaceOrderRequest request);

    OrderResponse cancelOrder(Long userId, Long orderId);

    OrderResponse track(Long userId, Long orderId);

    PagedResponse<OrderResponse> getMyOrders(Long userId, Pageable pageable);

    PagedResponse<OrderResponse> getRestaurantOrders(Long ownerId, Long restaurantId, Pageable pageable);

    OrderResponse updateStatusByOwner(Long ownerId, Long orderId, OrderStatus status);

    PagedResponse<OrderResponse> getAllOrders(Pageable pageable);

    OrderResponse updateStatusByAdmin(Long orderId, OrderStatus status);
}
