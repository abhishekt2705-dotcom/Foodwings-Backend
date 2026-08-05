package com.foodwings.service;

import com.foodwings.dto.request.RestaurantRequest;
import com.foodwings.dto.response.EarningsResponse;
import com.foodwings.dto.response.RestaurantResponse;
import com.foodwings.response.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RestaurantService {

    RestaurantResponse create(Long ownerId, RestaurantRequest request);

    RestaurantResponse update(Long ownerId, Long id, RestaurantRequest request);

    void delete(Long ownerId, Long id);

    RestaurantResponse getById(Long id);

    PagedResponse<RestaurantResponse> listApproved(Pageable pageable);

    PagedResponse<RestaurantResponse> search(String query, Pageable pageable);

    List<RestaurantResponse> getOwnerRestaurants(Long ownerId);

    RestaurantResponse uploadLogo(Long ownerId, Long id, MultipartFile file);

    RestaurantResponse uploadBanner(Long ownerId, Long id, MultipartFile file);

    EarningsResponse getEarnings(Long ownerId, Long restaurantId);

    // --- Admin operations ---

    PagedResponse<RestaurantResponse> listByStatus(String status, Pageable pageable);

    RestaurantResponse approve(Long id);

    RestaurantResponse reject(Long id);

    RestaurantResponse setActive(Long id, boolean active);
}
