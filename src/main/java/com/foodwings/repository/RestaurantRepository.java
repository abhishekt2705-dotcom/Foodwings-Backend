package com.foodwings.repository;

import com.foodwings.entity.Restaurant;
import com.foodwings.enums.RestaurantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Page<Restaurant> findByStatusAndActiveTrue(RestaurantStatus status, Pageable pageable);

    Page<Restaurant> findByStatus(RestaurantStatus status, Pageable pageable);

    List<Restaurant> findByOwnerId(Long ownerId);

    Page<Restaurant> findByStatusAndActiveTrueAndNameContainingIgnoreCase(
            RestaurantStatus status, String name, Pageable pageable);

    long countByStatus(RestaurantStatus status);
}
