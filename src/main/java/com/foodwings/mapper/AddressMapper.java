package com.foodwings.mapper;

import com.foodwings.dto.request.AddressRequest;
import com.foodwings.dto.response.AddressResponse;
import com.foodwings.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public Address toEntity(AddressRequest request) {
        return Address.builder()
                .label(request.getLabel())
                .line1(request.getLine1())
                .line2(request.getLine2())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .phone(request.getPhone())
                .defaultAddress(request.isDefaultAddress())
                .build();
    }

    public void updateEntity(Address address, AddressRequest request) {
        address.setLabel(request.getLabel());
        address.setLine1(request.getLine1());
        address.setLine2(request.getLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        address.setPhone(request.getPhone());
        address.setDefaultAddress(request.isDefaultAddress());
    }

    public AddressResponse toResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .label(address.getLabel())
                .line1(address.getLine1())
                .line2(address.getLine2())
                .city(address.getCity())
                .state(address.getState())
                .pincode(address.getPincode())
                .phone(address.getPhone())
                .defaultAddress(address.isDefaultAddress())
                .build();
    }
}