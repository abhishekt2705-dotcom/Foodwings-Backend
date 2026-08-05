package com.foodwings.mapper;

import com.foodwings.dto.response.AddressResponse;
import com.foodwings.dto.response.UserResponse;
import com.foodwings.entity.Address;
import com.foodwings.entity.Role;
import com.foodwings.entity.User;

import java.util.stream.Collectors;

/**
 * Maps {@link User}/{@link Address} entities to their response DTOs.
 */
public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profilePhoto(user.getProfilePhoto())
                .active(user.isActive())
                .roles(user.getRoles().stream().map(Role::getName).map(Enum::name).collect(Collectors.toSet()))
                .build();
    }

    public static AddressResponse toResponse(Address address) {
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
