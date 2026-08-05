package com.foodwings.service;

import com.foodwings.dto.response.WishlistResponse;

import java.util.List;

public interface WishlistService {

    WishlistResponse addFavorite(Long userId, Long restaurantId);

    void removeFavorite(Long userId, Long restaurantId);

    List<WishlistResponse> getFavorites(Long userId);
}
