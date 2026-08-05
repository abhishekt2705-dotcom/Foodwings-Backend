package com.foodwings.controller;

import com.foodwings.dto.request.CouponRequest;
import com.foodwings.dto.response.CouponResponse;
import com.foodwings.dto.response.DashboardResponse;
import com.foodwings.dto.response.DeliveryPartnerResponse;
import com.foodwings.dto.response.OrderResponse;
import com.foodwings.dto.response.RestaurantResponse;
import com.foodwings.dto.response.UserResponse;
import com.foodwings.enums.OrderStatus;
import com.foodwings.response.ApiResponse;
import com.foodwings.response.PagedResponse;
import com.foodwings.service.AdminService;
import com.foodwings.service.CategoryService;
import com.foodwings.service.CouponService;
import com.foodwings.service.OrderService;
import com.foodwings.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Platform administration: users, restaurants, coupons and reports")
public class AdminController {

    private final AdminService adminService;
    private final CategoryService categoryService;
    private final RestaurantService restaurantService;
    private final OrderService orderService;
    private final CouponService couponService;

    public AdminController(AdminService adminService,
                           CategoryService categoryService,
                           RestaurantService restaurantService,
                           OrderService orderService,
                           CouponService couponService) {
        this.adminService = adminService;
        this.categoryService = categoryService;
        this.restaurantService = restaurantService;
        this.orderService = orderService;
        this.couponService = couponService;
    }

    // ----- Dashboard / reports -----

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard metrics and total revenue")
    public ResponseEntity<ApiResponse<DashboardResponse>> dashboard() {
        return ResponseEntity.ok(ApiResponse.success("Dashboard fetched", adminService.getDashboard()));
    }

    // ----- User management -----

    @GetMapping("/users")
    @Operation(summary = "List users, optionally filtered by role")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> users(@RequestParam(required = false) String role,
                                                                          @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Users fetched", adminService.getUsers(role, pageable)));
    }

    @PutMapping("/user/{id}/status")
    @Operation(summary = "Activate or deactivate a user account")
    public ResponseEntity<ApiResponse<UserResponse>> setUserStatus(@PathVariable Long id, @RequestParam boolean active) {
        return ResponseEntity.ok(ApiResponse.success("User status updated", adminService.setUserActive(id, active)));
    }

    @DeleteMapping("/user/{id}")
    @Operation(summary = "Delete a user")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted"));
    }

    @GetMapping("/delivery-partners")
    @Operation(summary = "List delivery partners")
    public ResponseEntity<ApiResponse<List<DeliveryPartnerResponse>>> deliveryPartners() {
        return ResponseEntity.ok(ApiResponse.success("Delivery partners fetched", adminService.getDeliveryPartners()));
    }

    // ----- Restaurant management -----

    @GetMapping("/restaurants")
    @Operation(summary = "List restaurants, optionally filtered by status")
    public ResponseEntity<ApiResponse<PagedResponse<RestaurantResponse>>> restaurants(@RequestParam(required = false) String status,
                                                                                      @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Restaurants fetched", restaurantService.listByStatus(status, pageable)));
    }

    @PutMapping("/restaurant/{id}/approve")
    @Operation(summary = "Approve a restaurant")
    public ResponseEntity<ApiResponse<RestaurantResponse>> approve(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Restaurant approved", restaurantService.approve(id)));
    }

    @PutMapping("/restaurant/{id}/reject")
    @Operation(summary = "Reject a restaurant")
    public ResponseEntity<ApiResponse<RestaurantResponse>> reject(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Restaurant rejected", restaurantService.reject(id)));
    }

    @PutMapping("/restaurant/{id}/status")
    @Operation(summary = "Activate or deactivate a restaurant")
    public ResponseEntity<ApiResponse<RestaurantResponse>> setRestaurantActive(@PathVariable Long id, @RequestParam boolean active) {
        return ResponseEntity.ok(ApiResponse.success("Restaurant status updated", restaurantService.setActive(id, active)));
    }

    // ----- Order management -----

    @GetMapping("/orders")
    @Operation(summary = "List all orders")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> orders(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Orders fetched", orderService.getAllOrders(pageable)));
    }

    @PutMapping("/order/{id}/status")
    @Operation(summary = "Update the status of any order")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Order status updated", orderService.updateStatusByAdmin(id, status)));
    }

    // ----- Coupon management -----

    @GetMapping("/coupons")
    @Operation(summary = "List all coupons")
    public ResponseEntity<ApiResponse<List<CouponResponse>>> coupons() {
        return ResponseEntity.ok(ApiResponse.success("Coupons fetched", couponService.getAll()));
    }

    @PostMapping("/coupon")
    @Operation(summary = "Create a coupon")
    public ResponseEntity<ApiResponse<CouponResponse>> createCoupon(@Valid @RequestBody CouponRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Coupon created", couponService.create(request)));
    }

    @DeleteMapping("/coupon/{id}")
    @Operation(summary = "Delete a coupon")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(@PathVariable Long id) {
        couponService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Coupon deleted"));
    }
}
