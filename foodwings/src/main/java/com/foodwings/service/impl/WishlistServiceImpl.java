package com.foodwings.service.impl;

import com.foodwings.dto.response.WishlistResponse;
import com.foodwings.entity.Restaurant;
import com.foodwings.entity.User;
import com.foodwings.entity.Wishlist;
import com.foodwings.exception.DuplicateResourceException;
import com.foodwings.exception.ResourceNotFoundException;
import com.foodwings.repository.RestaurantRepository;
import com.foodwings.repository.UserRepository;
import com.foodwings.repository.WishlistRepository;
import com.foodwings.service.WishlistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public WishlistServiceImpl(WishlistRepository wishlistRepository,
                               RestaurantRepository restaurantRepository,
                               UserRepository userRepository) {
        this.wishlistRepository = wishlistRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    @Override
    public WishlistResponse addFavorite(Long userId, Long restaurantId) {
        if (wishlistRepository.existsByUserIdAndRestaurantId(userId, restaurantId)) {
            throw new DuplicateResourceException("Restaurant is already in your favorites");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", restaurantId));
        Wishlist wishlist = wishlistRepository.save(Wishlist.builder()
                .user(user)
                .restaurant(restaurant)
                .build());
        return toResponse(wishlist);
    }

    @Override
    public void removeFavorite(Long userId, Long restaurantId) {
        Wishlist wishlist = wishlistRepository.findByUserIdAndRestaurantId(userId, restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite restaurant not found"));
        wishlistRepository.delete(wishlist);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistResponse> getFavorites(Long userId) {
        return wishlistRepository.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    private WishlistResponse toResponse(Wishlist w) {
        Restaurant r = w.getRestaurant();
        return WishlistResponse.builder()
                .id(w.getId())
                .restaurantId(r.getId())
                .restaurantName(r.getName())
                .restaurantLogo(r.getLogo())
                .rating(r.getRating())
                .build();
    }
}
