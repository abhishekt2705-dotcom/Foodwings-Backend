package com.foodwings.service;

import com.foodwings.dto.request.FoodRequest;
import com.foodwings.dto.response.FoodResponse;
import com.foodwings.response.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FoodService {

    FoodResponse create(Long ownerId, boolean isAdmin, FoodRequest request);

    FoodResponse update(Long ownerId, boolean isAdmin, Long id, FoodRequest request);

    void delete(Long ownerId, boolean isAdmin, Long id);

    FoodResponse getById(Long id);

    PagedResponse<FoodResponse> list(Pageable pageable);

    PagedResponse<FoodResponse> search(String query, Pageable pageable);

    List<FoodResponse> getByRestaurant(Long restaurantId);

    List<FoodResponse> getBestSellers();

    List<FoodResponse> getPopular();

    FoodResponse uploadImage(Long ownerId, boolean isAdmin, Long id, MultipartFile file);
}
