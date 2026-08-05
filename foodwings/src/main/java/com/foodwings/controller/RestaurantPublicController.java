package com.foodwings.controller;

import com.foodwings.dto.response.FoodResponse;
import com.foodwings.dto.response.RestaurantResponse;
import com.foodwings.dto.response.ReviewResponse;
import com.foodwings.response.ApiResponse;
import com.foodwings.response.PagedResponse;
import com.foodwings.service.FoodService;
import com.foodwings.service.RestaurantService;
import com.foodwings.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@Tag(name = "Restaurants (public)", description = "Browse and search approved restaurants")
public class RestaurantPublicController {

    private final RestaurantService restaurantService;
    private final FoodService foodService;
    private final ReviewService reviewService;

    public RestaurantPublicController(RestaurantService restaurantService,
                                      FoodService foodService,
                                      ReviewService reviewService) {
        this.restaurantService = restaurantService;
        this.foodService = foodService;
        this.reviewService = reviewService;
    }

    @GetMapping
    @Operation(summary = "List approved restaurants")
    public ResponseEntity<ApiResponse<PagedResponse<RestaurantResponse>>> list(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Restaurants fetched", restaurantService.listApproved(pageable)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search approved restaurants by name")
    public ResponseEntity<ApiResponse<PagedResponse<RestaurantResponse>>> search(@RequestParam(name = "q", required = false) String query,
                                                                                 @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Search results", restaurantService.search(query, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a restaurant by id")
    public ResponseEntity<ApiResponse<RestaurantResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Restaurant fetched", restaurantService.getById(id)));
    }

    @GetMapping("/{id}/foods")
    @Operation(summary = "Get the menu of a restaurant")
    public ResponseEntity<ApiResponse<List<FoodResponse>>> getFoods(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Menu fetched", foodService.getByRestaurant(id)));
    }

    @GetMapping("/{id}/reviews")
    @Operation(summary = "Get reviews for a restaurant")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviews(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Reviews fetched", reviewService.getRestaurantReviews(id)));
    }
}
