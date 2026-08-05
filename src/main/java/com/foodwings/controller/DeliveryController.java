package com.foodwings.controller;

import com.foodwings.dto.request.UpdateDeliveryStatusRequest;
import com.foodwings.dto.response.OrderResponse;
import com.foodwings.response.ApiResponse;
import com.foodwings.response.PagedResponse;
import com.foodwings.security.CurrentUserService;
import com.foodwings.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/delivery")
@Tag(name = "Delivery Partner", description = "Accept, deliver and track deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final CurrentUserService currentUserService;

    public DeliveryController(DeliveryService deliveryService, CurrentUserService currentUserService) {
        this.deliveryService = deliveryService;
        this.currentUserService = currentUserService;
    }

    private Long partnerId() {
        return currentUserService.getCurrentUserId();
    }

    @GetMapping("/orders")
    @Operation(summary = "List orders that are ready and available for pickup")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> availableOrders(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Available orders fetched", deliveryService.getAvailableOrders(pageable)));
    }

    @GetMapping("/history")
    @Operation(summary = "List the current partner's deliveries")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> history(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Delivery history fetched", deliveryService.getMyDeliveries(partnerId(), pageable)));
    }

    @PutMapping("/accept/{orderId}")
    @Operation(summary = "Accept a delivery")
    public ResponseEntity<ApiResponse<OrderResponse>> accept(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success("Delivery accepted", deliveryService.acceptDelivery(partnerId(), orderId)));
    }

    @PutMapping("/reject/{orderId}")
    @Operation(summary = "Reject / release a delivery")
    public ResponseEntity<ApiResponse<OrderResponse>> reject(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success("Delivery released", deliveryService.rejectDelivery(partnerId(), orderId)));
    }

    @PutMapping("/status")
    @Operation(summary = "Update delivery status (OUT_FOR_DELIVERY or DELIVERED)")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(@Valid @RequestBody UpdateDeliveryStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Status updated",
                deliveryService.updateStatus(partnerId(), request.getOrderId(), request.getStatus())));
    }

    @PutMapping("/delivered/{orderId}")
    @Operation(summary = "Mark an order as delivered")
    public ResponseEntity<ApiResponse<OrderResponse>> delivered(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success("Order delivered", deliveryService.markDelivered(partnerId(), orderId)));
    }
}
