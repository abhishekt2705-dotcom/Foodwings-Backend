package com.foodwings.service;

import com.foodwings.dto.request.AddressRequest;
import com.foodwings.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {

    List<AddressResponse> getMyAddresses(Long userId);

    AddressResponse getAddressById(Long userId, Long addressId);

    AddressResponse addAddress(Long userId, AddressRequest request);

    AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request);

    void deleteAddress(Long userId, Long addressId);

    AddressResponse setDefaultAddress(Long userId, Long addressId);
}