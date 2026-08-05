package com.foodwings.service.impl;

import com.foodwings.dto.response.DashboardResponse;
import com.foodwings.dto.response.DeliveryPartnerResponse;
import com.foodwings.dto.response.UserResponse;
import com.foodwings.entity.User;
import com.foodwings.enums.OrderStatus;
import com.foodwings.enums.RestaurantStatus;
import com.foodwings.enums.RoleName;
import com.foodwings.exception.BadRequestException;
import com.foodwings.exception.ResourceNotFoundException;
import com.foodwings.mapper.DeliveryPartnerMapper;
import com.foodwings.mapper.UserMapper;
import com.foodwings.repository.DeliveryPartnerRepository;
import com.foodwings.repository.OrderRepository;
import com.foodwings.repository.RestaurantRepository;
import com.foodwings.repository.UserRepository;
import com.foodwings.response.PagedResponse;
import com.foodwings.service.AdminService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;
    private final DeliveryPartnerRepository deliveryPartnerRepository;

    public AdminServiceImpl(UserRepository userRepository,
                            RestaurantRepository restaurantRepository,
                            OrderRepository orderRepository,
                            DeliveryPartnerRepository deliveryPartnerRepository) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderRepository = orderRepository;
        this.deliveryPartnerRepository = deliveryPartnerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        return DashboardResponse.builder()
                .totalCustomers(userRepository.countByRole(RoleName.CUSTOMER))
                .totalRestaurantOwners(userRepository.countByRole(RoleName.RESTAURANT_OWNER))
                .totalDeliveryPartners(userRepository.countByRole(RoleName.DELIVERY_PARTNER))
                .totalRestaurants(restaurantRepository.count())
                .pendingRestaurants(restaurantRepository.countByStatus(RestaurantStatus.PENDING))
                .totalOrders(orderRepository.count())
                .deliveredOrders(orderRepository.countByStatus(OrderStatus.DELIVERED))
                .cancelledOrders(orderRepository.countByStatus(OrderStatus.CANCELLED))
                .totalRevenue(orderRepository.calculateTotalRevenue())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> getUsers(String role, Pageable pageable) {
        if (role == null || role.isBlank()) {
            return PagedResponse.from(userRepository.findAll(pageable), UserMapper::toResponse);
        }
        RoleName roleName = parseRole(role);
        return PagedResponse.from(userRepository.findByRole(roleName, pageable), UserMapper::toResponse);
    }

    @Override
    public UserResponse setUserActive(Long userId, boolean active) {
        User user = findUser(userId);
        user.setActive(active);
        return UserMapper.toResponse(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long userId) {
        User user = findUser(userId);
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryPartnerResponse> getDeliveryPartners() {
        return deliveryPartnerRepository.findAll().stream().map(DeliveryPartnerMapper::toResponse).toList();
    }

    private RoleName parseRole(String role) {
        try {
            return RoleName.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid role: " + role);
        }
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
}
