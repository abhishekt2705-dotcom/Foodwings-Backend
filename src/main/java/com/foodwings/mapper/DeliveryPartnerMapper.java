package com.foodwings.mapper;

import com.foodwings.dto.response.DeliveryPartnerResponse;
import com.foodwings.entity.DeliveryPartner;

/**
 * Maps {@link DeliveryPartner} entities to response DTOs.
 */
public final class DeliveryPartnerMapper {

    private DeliveryPartnerMapper() {
    }

    public static DeliveryPartnerResponse toResponse(DeliveryPartner dp) {
        return DeliveryPartnerResponse.builder()
                .id(dp.getId())
                .userId(dp.getUser().getId())
                .name(dp.getUser().getName())
                .phone(dp.getUser().getPhone())
                .vehicleNumber(dp.getVehicleNumber())
                .currentLocation(dp.getCurrentLocation())
                .available(dp.isAvailable())
                .totalDeliveries(dp.getTotalDeliveries())
                .build();
    }
}
