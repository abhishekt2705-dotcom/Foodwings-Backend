package com.foodwings.service;

import com.foodwings.dto.request.AddressRequest;
import com.foodwings.dto.request.UpdateProfileRequest;
import com.foodwings.dto.response.AddressResponse;
import com.foodwings.dto.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserResponse getProfile(Long userId);

    UserResponse updateProfile(Long userId, UpdateProfileRequest request);

    UserResponse uploadProfilePhoto(Long userId, MultipartFile file);

    AddressResponse addAddress(Long userId, AddressRequest request);

    List<AddressResponse> getAddresses(Long userId);

    AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request);

    void deleteAddress(Long userId, Long addressId);
}
