package com.foodwings.service;

import com.foodwings.dto.request.ReviewRequest;
import com.foodwings.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {

    ReviewResponse addReview(Long userId, ReviewRequest request);

    List<ReviewResponse> getRestaurantReviews(Long restaurantId);

    List<ReviewResponse> getFoodReviews(Long foodItemId);

    List<ReviewResponse> getMyReviews(Long userId);
}
