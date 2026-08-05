package com.foodwings.controller;

import com.foodwings.dto.request.FoodRequest;
import com.foodwings.dto.request.RestaurantRequest;
import com.foodwings.dto.response.EarningsResponse;
import com.foodwings.dto.response.FoodResponse;
import com.foodwings.dto.response.OrderResponse;
import com.foodwings.dto.response.RestaurantResponse;
import com.foodwings.enums.OrderStatus;
import com.foodwings.response.ApiResponse;
import com.foodwings.response.PagedResponse;
import com.foodwings.security.CurrentUserService;
import com.foodwings.service.FoodService;
import com.foodwings.service.OrderService;
import com.foodwings.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/restaurant")
@Tag(name = "Restaurant Owner", description = "Manage restaurants, menu, orders and earnings")
public class RestaurantOwnerController {

    private final RestaurantService restaurantService;
    private final FoodService foodService;
    private final OrderService orderService;
    private final CurrentUserService currentUserService;

    public RestaurantOwnerController(
            RestaurantService restaurantService,
            FoodService foodService,
            OrderService orderService,
            CurrentUserService currentUserService) {

        this.restaurantService = restaurantService;
        this.foodService = foodService;
        this.orderService = orderService;
        this.currentUserService = currentUserService;
    }

    private Long ownerId() {
        return currentUserService.getCurrentUserId();
    }

    // =====================================================
    // Restaurant Management
    // =====================================================

    @PostMapping
    @Operation(summary = "Register a new restaurant")
    public ResponseEntity<ApiResponse<RestaurantResponse>> create(
            @Valid @RequestBody RestaurantRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Restaurant registered (pending approval)",
                        restaurantService.create(ownerId(), request)
                ));
    }

    @GetMapping("/mine")
    @Operation(summary = "List restaurants owned by current owner")
    public ResponseEntity<ApiResponse<List<RestaurantResponse>>> myRestaurants() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Restaurants fetched",
                        restaurantService.getOwnerRestaurants(ownerId())
                )
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update restaurant")
    public ResponseEntity<ApiResponse<RestaurantResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Restaurant updated",
                        restaurantService.update(ownerId(), id, request)
                )
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete restaurant")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id) {

        restaurantService.delete(ownerId(), id);

        return ResponseEntity.ok(
                ApiResponse.success("Restaurant deleted")
        );
    }

    @PostMapping("/{id}/logo")
    @Operation(summary = "Upload restaurant logo")
    public ResponseEntity<ApiResponse<RestaurantResponse>> uploadLogo(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Logo uploaded",
                        restaurantService.uploadLogo(ownerId(), id, file)
                )
        );
    }

    @PostMapping("/{id}/banner")
    @Operation(summary = "Upload restaurant banner")
    public ResponseEntity<ApiResponse<RestaurantResponse>> uploadBanner(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Banner uploaded",
                        restaurantService.uploadBanner(ownerId(), id, file)
                )
        );
    }

    @GetMapping("/{id}/earnings")
    @Operation(summary = "Restaurant earnings")
    public ResponseEntity<ApiResponse<EarningsResponse>> earnings(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Earnings fetched",
                        restaurantService.getEarnings(ownerId(), id)
                )
        );
    }

    // =====================================================
    // Food Management
    // =====================================================

    @GetMapping("/{restaurantId}/foods")
    @Operation(summary = "Get all foods of a restaurant")
    public ResponseEntity<ApiResponse<List<FoodResponse>>> getRestaurantFoods(
            @PathVariable Long restaurantId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Foods fetched successfully",
                        foodService.getByRestaurant(restaurantId)
                )
        );
    }

    @PostMapping("/food")
    @Operation(summary = "Add food")
    public ResponseEntity<ApiResponse<FoodResponse>> addFood(
            @Valid @RequestBody FoodRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Food added",
                        foodService.create(
                                ownerId(),
                                currentUserService.isAdmin(),
                                request
                        )
                ));
    }

    @PutMapping("/food/{id}")
    @Operation(summary = "Update food")
    public ResponseEntity<ApiResponse<FoodResponse>> updateFood(
            @PathVariable Long id,
            @Valid @RequestBody FoodRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Food updated",
                        foodService.update(
                                ownerId(),
                                currentUserService.isAdmin(),
                                id,
                                request
                        )
                )
        );
    }

    @DeleteMapping("/food/{id}")
    @Operation(summary = "Delete food")
    public ResponseEntity<ApiResponse<Void>> deleteFood(
            @PathVariable Long id) {

        foodService.delete(
                ownerId(),
                currentUserService.isAdmin(),
                id
        );

        return ResponseEntity.ok(
                ApiResponse.success("Food deleted")
        );
    }

    @PostMapping("/food/{id}/image")
    @Operation(summary = "Upload food image")
    public ResponseEntity<ApiResponse<FoodResponse>> uploadFoodImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Food image uploaded",
                        foodService.uploadImage(
                                ownerId(),
                                currentUserService.isAdmin(),
                                id,
                                file
                        )
                )
        );
    }

    // =====================================================
    // Order Management
    // =====================================================

    @GetMapping("/{id}/orders")
    @Operation(summary = "Restaurant orders")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> orders(
            @PathVariable Long id,
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Orders fetched",
                        orderService.getRestaurantOrders(
                                ownerId(),
                                id,
                                pageable
                        )
                )
        );
    }

    @PutMapping("/order/{orderId}/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Order status updated",
                        orderService.updateStatusByOwner(
                                ownerId(),
                                orderId,
                                status
                        )
                )
        );
    }
}