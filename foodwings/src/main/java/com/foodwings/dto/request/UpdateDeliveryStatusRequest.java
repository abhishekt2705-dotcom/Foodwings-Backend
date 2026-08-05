package com.foodwings.dto.request;

import com.foodwings.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
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
public class UpdateDeliveryStatusRequest {

    @NotNull(message = "Order id is required")
    private Long orderId;

    @NotNull(message = "Status is required")
    private OrderStatus status;

    // Explicit getters for IDEs that may not recognise Lombok-generated methods
    public Long getOrderId() {
        return this.orderId;
    }

    public OrderStatus getStatus() {
        return this.status;
    }
}
