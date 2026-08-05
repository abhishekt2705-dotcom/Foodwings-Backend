package com.foodwings.service.impl;

import com.foodwings.dto.request.AddressRequest;
import com.foodwings.dto.request.UpdateProfileRequest;
import com.foodwings.dto.response.AddressResponse;
import com.foodwings.dto.response.UserResponse;
import com.foodwings.entity.Address;
import com.foodwings.entity.User;
import com.foodwings.exception.ResourceNotFoundException;
import com.foodwings.mapper.UserMapper;
import com.foodwings.repository.AddressRepository;
import com.foodwings.repository.UserRepository;
import com.foodwings.service.FileStorageService;
import com.foodwings.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final FileStorageService fileStorageService;

    public UserServiceImpl(UserRepository userRepository,
                           AddressRepository addressRepository,
                           FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getProfile(Long userId) {
        return UserMapper.toResponse(findUser(userId));
    }

    @Override
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = findUser(userId);
        if (StringUtils.hasText(request.getName())) {
            user.setName(request.getName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        return UserMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse uploadProfilePhoto(Long userId, MultipartFile file) {
        User user = findUser(userId);
        String path = fileStorageService.store(file, "profiles");
        user.setProfilePhoto(path);
        return UserMapper.toResponse(userRepository.save(user));
    }

    @Override
    public AddressResponse addAddress(Long userId, AddressRequest request) {
        User user = findUser(userId);
        Address address = Address.builder()
                .user(user)
                .label(request.getLabel())
                .line1(request.getLine1())
                .line2(request.getLine2())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .phone(request.getPhone())
                .defaultAddress(request.isDefaultAddress())
                .build();
        if (request.isDefaultAddress()) {
            clearDefault(userId);
        }
        return UserMapper.toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getAddresses(Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Override
    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request) {
        Address address = findAddress(userId, addressId);
        address.setLabel(request.getLabel());
        address.setLine1(request.getLine1());
        address.setLine2(request.getLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        address.setPhone(request.getPhone());
        if (request.isDefaultAddress()) {
            clearDefault(userId);
        }
        address.setDefaultAddress(request.isDefaultAddress());
        return UserMapper.toResponse(addressRepository.save(address));
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        Address address = findAddress(userId, addressId);
        addressRepository.delete(address);
    }

    private void clearDefault(Long userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        addresses.forEach(a -> a.setDefaultAddress(false));
        addressRepository.saveAll(addresses);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    private Address findAddress(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        if (!address.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Address", "id", addressId);
        }
        return address;
    }
}
