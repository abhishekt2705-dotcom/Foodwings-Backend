package com.foodwings.repository;

import com.foodwings.entity.FoodItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    Page<FoodItem> findByAvailableTrue(Pageable pageable);

    Page<FoodItem> findByAvailableTrueAndNameContainingIgnoreCase(String name, Pageable pageable);

    Page<FoodItem> findByRestaurantId(Long restaurantId, Pageable pageable);

    List<FoodItem> findByRestaurantId(Long restaurantId);

    Page<FoodItem> findByCategoryId(Long categoryId, Pageable pageable);

    List<FoodItem> findByBestSellerTrueAndAvailableTrue();

    List<FoodItem> findByPopularTrueAndAvailableTrue();
}
