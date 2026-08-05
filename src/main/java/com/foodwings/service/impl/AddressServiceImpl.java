package com.foodwings.service.impl;

import com.foodwings.dto.request.AddressRequest;
import com.foodwings.dto.response.AddressResponse;
import com.foodwings.entity.Address;
import com.foodwings.entity.User;
import com.foodwings.exception.ResourceNotFoundException;
import com.foodwings.mapper.AddressMapper;
import com.foodwings.repository.AddressRepository;
import com.foodwings.repository.UserRepository;
import com.foodwings.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    public AddressServiceImpl(AddressRepository addressRepository,
                              UserRepository userRepository,
                              AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.addressMapper = addressMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getMyAddresses(Long userId) {

        return addressRepository.findByUserId(userId)
                .stream()
                .map(addressMapper::toResponse)
                .toList();
    }

    // ===================== NEW METHOD =====================
    @Override
    @Transactional(readOnly = true)
    public AddressResponse getAddressById(Long userId, Long addressId) {

        Address address = getAddress(userId, addressId);

        return addressMapper.toResponse(address);
    }
    // ======================================================

    @Override
    public AddressResponse addAddress(Long userId, AddressRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", "id", userId));

        if (request.isDefaultAddress()) {
            clearDefaultAddress(userId);
        }

        Address address = addressMapper.toEntity(request);
        address.setUser(user);

        Address saved = addressRepository.save(address);

        return addressMapper.toResponse(saved);
    }

    @Override
    public AddressResponse updateAddress(Long userId,
                                         Long addressId,
                                         AddressRequest request) {

        Address address = getAddress(userId, addressId);

        if (request.isDefaultAddress()) {
            clearDefaultAddress(userId);
        }

        addressMapper.updateEntity(address, request);

        Address updated = addressRepository.save(address);

        return addressMapper.toResponse(updated);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {

        Address address = getAddress(userId, addressId);

        addressRepository.delete(address);
    }

    @Override
    public AddressResponse setDefaultAddress(Long userId, Long addressId) {

        clearDefaultAddress(userId);

        Address address = getAddress(userId, addressId);

        address.setDefaultAddress(true);

        Address saved = addressRepository.save(address);

        return addressMapper.toResponse(saved);
    }

    private void clearDefaultAddress(Long userId) {

        List<Address> addresses = addressRepository.findByUserId(userId);

        for (Address address : addresses) {
            if (address.isDefaultAddress()) {
                address.setDefaultAddress(false);
                addressRepository.save(address);
            }
        }
    }

    private Address getAddress(Long userId, Long addressId) {

        return addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address",
                                "id",
                                addressId
                        ));
    }
}