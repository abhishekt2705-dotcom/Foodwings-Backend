package com.foodwings.dto.response;

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
public class DeliveryPartnerResponse {
    private Long id;
    private Long userId;
    private String name;
    private String phone;
    private String vehicleNumber;
    private String currentLocation;
    private boolean available;
    private int totalDeliveries;
}
