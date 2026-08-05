package com.foodwings.service;

import com.foodwings.dto.response.DashboardResponse;
import com.foodwings.dto.response.DeliveryPartnerResponse;
import com.foodwings.dto.response.UserResponse;
import com.foodwings.response.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {

    DashboardResponse getDashboard();

    PagedResponse<UserResponse> getUsers(String role, Pageable pageable);

    UserResponse setUserActive(Long userId, boolean active);

    void deleteUser(Long userId);

    List<DeliveryPartnerResponse> getDeliveryPartners();
}
