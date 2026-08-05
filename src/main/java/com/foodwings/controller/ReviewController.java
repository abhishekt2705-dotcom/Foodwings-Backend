package com.foodwings.controller;

import com.foodwings.dto.request.ReviewRequest;
import com.foodwings.dto.response.ReviewResponse;
import com.foodwings.response.ApiResponse;
import com.foodwings.security.CurrentUserService;
import com.foodwings.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Reviews", description = "Rate and review restaurants and food items")
public class ReviewController {

    private final ReviewService reviewService;
    private final CurrentUserService currentUserService;

    public ReviewController(ReviewService reviewService, CurrentUserService currentUserService) {
        this.reviewService = reviewService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @Operation(summary = "Add a review for a restaurant or food item")
    public ResponseEntity<ApiResponse<ReviewResponse>> add(@Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review added",
                        reviewService.addReview(currentUserService.getCurrentUserId(), request)));
    }

    @GetMapping("/mine")
    @Operation(summary = "List the current user's reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> myReviews() {
        return ResponseEntity.ok(ApiResponse.success("Reviews fetched",
                reviewService.getMyReviews(currentUserService.getCurrentUserId())));
    }
}
