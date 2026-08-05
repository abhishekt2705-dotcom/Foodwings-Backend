package com.foodwings.controller;

import com.foodwings.dto.request.AddToCartRequest;
import com.foodwings.dto.request.ApplyCouponRequest;
import com.foodwings.dto.request.UpdateCartItemRequest;
import com.foodwings.dto.response.CartResponse;
import com.foodwings.response.ApiResponse;
import com.foodwings.security.CurrentUserService;
import com.foodwings.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "Customer shopping cart operations")
public class CartController {

    private final CartService cartService;
    private final CurrentUserService currentUserService;

    public CartController(CartService cartService, CurrentUserService currentUserService) {
        this.cartService = cartService;
        this.currentUserService = currentUserService;
    }

    private Long userId() {
        return currentUserService.getCurrentUserId();
    }

    @GetMapping
    @Operation(summary = "Get the current user's cart")
    public ResponseEntity<ApiResponse<CartResponse>> getCart() {
        return ResponseEntity.ok(ApiResponse.success("Cart fetched", cartService.getCart(userId())));
    }

    @PostMapping("/add")
    @Operation(summary = "Add an item to the cart")
    public ResponseEntity<ApiResponse<CartResponse>> add(@Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Item added to cart", cartService.addItem(userId(), request)));
    }

    @PutMapping("/item")
    @Operation(summary = "Update the quantity of a cart item (0 removes it)")
    public ResponseEntity<ApiResponse<CartResponse>> update(@Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cart updated", cartService.updateItem(userId(), request)));
    }

    @DeleteMapping("/item/{foodItemId}")
    @Operation(summary = "Remove an item from the cart")
    public ResponseEntity<ApiResponse<CartResponse>> remove(@PathVariable Long foodItemId) {
        return ResponseEntity.ok(ApiResponse.success("Item removed", cartService.removeItem(userId(), foodItemId)));
    }

    @PostMapping("/coupon")
    @Operation(summary = "Apply a coupon to the cart")
    public ResponseEntity<ApiResponse<CartResponse>> applyCoupon(@Valid @RequestBody ApplyCouponRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Coupon applied", cartService.applyCoupon(userId(), request.getCode())));
    }

    @DeleteMapping("/coupon")
    @Operation(summary = "Remove the applied coupon")
    public ResponseEntity<ApiResponse<CartResponse>> removeCoupon() {
        return ResponseEntity.ok(ApiResponse.success("Coupon removed", cartService.removeCoupon(userId())));
    }

    @DeleteMapping
    @Operation(summary = "Clear the cart")
    public ResponseEntity<ApiResponse<Void>> clear() {
        cartService.clearCart(userId());
        return ResponseEntity.ok(ApiResponse.success("Cart cleared"));
    }
}
