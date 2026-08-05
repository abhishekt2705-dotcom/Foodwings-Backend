package com.foodwings.controller;

import com.foodwings.dto.response.FoodResponse;
import com.foodwings.dto.response.ReviewResponse;
import com.foodwings.response.ApiResponse;
import com.foodwings.response.PagedResponse;
import com.foodwings.service.FoodService;
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
@RequestMapping("/api/foods")
@Tag(name = "Foods (public)", description = "Browse, search and view food items")
public class FoodController {

    private final FoodService foodService;
    private final ReviewService reviewService;

    public FoodController(FoodService foodService, ReviewService reviewService) {
        this.foodService = foodService;
        this.reviewService = reviewService;
    }

    @GetMapping
    @Operation(summary = "List available food items")
    public ResponseEntity<ApiResponse<PagedResponse<FoodResponse>>> list(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Foods fetched", foodService.list(pageable)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search food items by name")
    public ResponseEntity<ApiResponse<PagedResponse<FoodResponse>>> search(@RequestParam(name = "q", required = false) String query,
                                                                           @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Search results", foodService.search(query, pageable)));
    }

    @GetMapping("/best-sellers")
    @Operation(summary = "List best-seller food items")
    public ResponseEntity<ApiResponse<List<FoodResponse>>> bestSellers() {
        return ResponseEntity.ok(ApiResponse.success("Best sellers fetched", foodService.getBestSellers()));
    }

    @GetMapping("/popular")
    @Operation(summary = "List popular food items")
    public ResponseEntity<ApiResponse<List<FoodResponse>>> popular() {
        return ResponseEntity.ok(ApiResponse.success("Popular foods fetched", foodService.getPopular()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a food item by id")
    public ResponseEntity<ApiResponse<FoodResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Food fetched", foodService.getById(id)));
    }

    @GetMapping("/{id}/reviews")
    @Operation(summary = "Get reviews for a food item")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> reviews(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Reviews fetched", reviewService.getFoodReviews(id)));
    }
}
