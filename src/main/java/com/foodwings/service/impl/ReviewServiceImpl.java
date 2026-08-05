package com.foodwings.service.impl;

import com.foodwings.dto.request.ReviewRequest;
import com.foodwings.dto.response.ReviewResponse;
import com.foodwings.entity.FoodItem;
import com.foodwings.entity.Restaurant;
import com.foodwings.entity.Review;
import com.foodwings.entity.User;
import com.foodwings.exception.BadRequestException;
import com.foodwings.exception.ResourceNotFoundException;
import com.foodwings.mapper.ReviewMapper;
import com.foodwings.repository.FoodItemRepository;
import com.foodwings.repository.RestaurantRepository;
import com.foodwings.repository.ReviewRepository;
import com.foodwings.repository.UserRepository;
import com.foodwings.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodItemRepository foodItemRepository;
    private final UserRepository userRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             RestaurantRepository restaurantRepository,
                             FoodItemRepository foodItemRepository,
                             UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.restaurantRepository = restaurantRepository;
        this.foodItemRepository = foodItemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ReviewResponse addReview(Long userId, ReviewRequest request) {
        if (request.getRestaurantId() == null && request.getFoodItemId() == null) {
            throw new BadRequestException("Provide either a restaurantId or a foodItemId to review");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Review.ReviewBuilder builder = Review.builder()
                .user(user)
                .rating(request.getRating())
                .comment(request.getComment());

        Restaurant restaurant = null;
        FoodItem food = null;
        if (request.getRestaurantId() != null) {
            restaurant = restaurantRepository.findById(request.getRestaurantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", request.getRestaurantId()));
            builder.restaurant(restaurant);
        }
        if (request.getFoodItemId() != null) {
            food = foodItemRepository.findById(request.getFoodItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("FoodItem", "id", request.getFoodItemId()));
            builder.foodItem(food);
        }

        Review saved = reviewRepository.save(builder.build());

        if (restaurant != null) {
            recalculateRestaurantRating(restaurant);
        }
        if (food != null) {
            recalculateFoodRating(food);
        }
        return ReviewMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getRestaurantReviews(Long restaurantId) {
        return reviewRepository.findByRestaurantId(restaurantId).stream().map(ReviewMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getFoodReviews(Long foodItemId) {
        return reviewRepository.findByFoodItemId(foodItemId).stream().map(ReviewMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getMyReviews(Long userId) {
        return reviewRepository.findByUserId(userId).stream().map(ReviewMapper::toResponse).toList();
    }

    private void recalculateRestaurantRating(Restaurant restaurant) {
        Double avg = reviewRepository.averageRatingForRestaurant(restaurant.getId());
        long count = reviewRepository.countByRestaurantId(restaurant.getId());
        restaurant.setRating(avg == null ? 0.0 : Math.round(avg * 10.0) / 10.0);
        restaurant.setTotalReviews((int) count);
        restaurantRepository.save(restaurant);
    }

    private void recalculateFoodRating(FoodItem food) {
        Double avg = reviewRepository.averageRatingForFood(food.getId());
        food.setRating(avg == null ? 0.0 : Math.round(avg * 10.0) / 10.0);
        foodItemRepository.save(food);
    }
}
