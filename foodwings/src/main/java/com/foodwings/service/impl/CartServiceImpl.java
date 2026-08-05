package com.foodwings.service.impl;

import com.foodwings.dto.request.AddToCartRequest;
import com.foodwings.dto.request.UpdateCartItemRequest;
import com.foodwings.dto.response.CartResponse;
import com.foodwings.entity.Cart;
import com.foodwings.entity.CartItem;
import com.foodwings.entity.Coupon;
import com.foodwings.entity.FoodItem;
import com.foodwings.entity.User;
import com.foodwings.exception.BadRequestException;
import com.foodwings.exception.ResourceNotFoundException;
import com.foodwings.mapper.CartMapper;
import com.foodwings.mapper.FoodMapper;
import com.foodwings.repository.CartRepository;
import com.foodwings.repository.FoodItemRepository;
import com.foodwings.repository.UserRepository;
import com.foodwings.service.CartService;
import com.foodwings.service.CouponService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final FoodItemRepository foodItemRepository;
    private final UserRepository userRepository;
    private final CouponService couponService;

    public CartServiceImpl(CartRepository cartRepository,
                           FoodItemRepository foodItemRepository,
                           UserRepository userRepository,
                           CouponService couponService) {
        this.cartRepository = cartRepository;
        this.foodItemRepository = foodItemRepository;
        this.userRepository = userRepository;
        this.couponService = couponService;
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        return CartMapper.toResponse(getOrCreateCartEntity(userId));
    }

    @Override
    public CartResponse addItem(Long userId, AddToCartRequest request) {
        Cart cart = getOrCreateCartEntity(userId);
        FoodItem food = foodItemRepository.findById(request.getFoodItemId())
                .orElseThrow(() -> new ResourceNotFoundException("FoodItem", "id", request.getFoodItemId()));
        if (!food.isAvailable()) {
            throw new BadRequestException("Food item is not available: " + food.getName());
        }
        // All items in a cart must belong to the same restaurant
        if (!cart.getItems().isEmpty()) {
            Long existingRestaurant = cart.getItems().get(0).getFoodItem().getRestaurant().getId();
            if (!existingRestaurant.equals(food.getRestaurant().getId())) {
                throw new BadRequestException("Cart can only contain items from a single restaurant. Clear the cart first.");
            }
        }

        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getFoodItem().getId().equals(food.getId()))
                .findFirst();
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            CartItem item = CartItem.builder()
                    .foodItem(food)
                    .quantity(request.getQuantity())
                    .price(FoodMapper.effectivePrice(food))
                    .build();
            cart.addItem(item);
        }
        recalculate(cart);
        return CartMapper.toResponse(cart);
    }

    @Override
    public CartResponse updateItem(Long userId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCartEntity(userId);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getFoodItem().getId().equals(request.getFoodItemId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item", "foodItemId", request.getFoodItemId()));
        if (request.getQuantity() <= 0) {
            cart.removeItem(item);
        } else {
            item.setQuantity(request.getQuantity());
        }
        recalculate(cart);
        return CartMapper.toResponse(cart);
    }

    @Override
    public CartResponse removeItem(Long userId, Long foodItemId) {
        Cart cart = getOrCreateCartEntity(userId);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getFoodItem().getId().equals(foodItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item", "foodItemId", foodItemId));
        cart.removeItem(item);
        recalculate(cart);
        return CartMapper.toResponse(cart);
    }

    @Override
    public CartResponse applyCoupon(Long userId, String code) {
        Cart cart = getOrCreateCartEntity(userId);
        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cannot apply a coupon to an empty cart");
        }
        Coupon coupon = couponService.validate(code, cart.getTotalAmount());
        cart.setCoupon(coupon);
        recalculate(cart);
        return CartMapper.toResponse(cart);
    }

    @Override
    public CartResponse removeCoupon(Long userId) {
        Cart cart = getOrCreateCartEntity(userId);
        cart.setCoupon(null);
        recalculate(cart);
        return CartMapper.toResponse(cart);
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCartEntity(userId);
        cart.getItems().clear();
        cart.setCoupon(null);
        recalculate(cart);
    }

    @Override
    public Cart getOrCreateCartEntity(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
            Cart cart = Cart.builder().user(user).build();
            return cartRepository.save(cart);
        });
    }

    @Override
    public void recalculate(Cart cart) {
        BigDecimal total = cart.getItems().stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalAmount(total);

        BigDecimal discount = BigDecimal.ZERO;
        Coupon coupon = cart.getCoupon();
        if (coupon != null) {
            if (total.compareTo(coupon.getMinOrderAmount()) < 0 || !coupon.isActive()) {
                cart.setCoupon(null);
            } else {
                discount = couponService.computeDiscount(coupon, total);
            }
        }
        cart.setDiscountAmount(discount);
        cart.setFinalAmount(total.subtract(discount).max(BigDecimal.ZERO));
        cartRepository.save(cart);
    }
}
