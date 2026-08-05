package com.foodwings.service;

import com.foodwings.dto.response.OrderResponse;
import com.foodwings.enums.OrderStatus;
import com.foodwings.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface DeliveryService {

    PagedResponse<OrderResponse> getAvailableOrders(Pageable pageable);

    PagedResponse<OrderResponse> getMyDeliveries(Long partnerId, Pageable pageable);

    OrderResponse acceptDelivery(Long partnerId, Long orderId);

    OrderResponse rejectDelivery(Long partnerId, Long orderId);

    OrderResponse updateStatus(Long partnerId, Long orderId, OrderStatus status);

    OrderResponse markDelivered(Long partnerId, Long orderId);
}
