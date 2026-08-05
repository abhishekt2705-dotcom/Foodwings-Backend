package com.foodwings.repository;

import com.foodwings.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByRestaurantId(Long restaurantId);

    List<Review> findByFoodItemId(Long foodItemId);

    List<Review> findByUserId(Long userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.restaurant.id = :restaurantId")
    Double averageRatingForRestaurant(@Param("restaurantId") Long restaurantId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.foodItem.id = :foodItemId")
    Double averageRatingForFood(@Param("foodItemId") Long foodItemId);

    long countByRestaurantId(Long restaurantId);
}
