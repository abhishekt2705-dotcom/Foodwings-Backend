package com.foodwings.controller;

import com.foodwings.dto.response.WishlistResponse;
import com.foodwings.response.ApiResponse;
import com.foodwings.security.CurrentUserService;
import com.foodwings.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@Tag(name = "Wishlist", description = "Favorite restaurants")
public class WishlistController {

    private final WishlistService wishlistService;
    private final CurrentUserService currentUserService;

    public WishlistController(WishlistService wishlistService, CurrentUserService currentUserService) {
        this.wishlistService = wishlistService;
        this.currentUserService = currentUserService;
    }

    private Long userId() {
        return currentUserService.getCurrentUserId();
    }

    @GetMapping
    @Operation(summary = "List favorite restaurants")
    public ResponseEntity<ApiResponse<List<WishlistResponse>>> list() {
        return ResponseEntity.ok(ApiResponse.success("Favorites fetched", wishlistService.getFavorites(userId())));
    }

    @PostMapping("/{restaurantId}")
    @Operation(summary = "Add a restaurant to favorites")
    public ResponseEntity<ApiResponse<WishlistResponse>> add(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(ApiResponse.success("Added to favorites",
                wishlistService.addFavorite(userId(), restaurantId)));
    }

    @DeleteMapping("/{restaurantId}")
    @Operation(summary = "Remove a restaurant from favorites")
    public ResponseEntity<ApiResponse<Void>> remove(@PathVariable Long restaurantId) {
        wishlistService.removeFavorite(userId(), restaurantId);
        return ResponseEntity.ok(ApiResponse.success("Removed from favorites"));
    }
}
