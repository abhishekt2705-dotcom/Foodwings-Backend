package com.foodwings.repository;

import com.foodwings.entity.Order;
import com.foodwings.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findByCustomerIdOrderByIdDesc(Long customerId, Pageable pageable);

    Page<Order> findByRestaurantIdOrderByIdDesc(Long restaurantId, Pageable pageable);

    Page<Order> findByDeliveryPartnerIdOrderByIdDesc(Long deliveryPartnerId, Pageable pageable);

    Page<Order> findByStatusOrderByIdDesc(OrderStatus status, Pageable pageable);

    long countByStatus(OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Order o WHERE o.status = com.foodwings.enums.OrderStatus.DELIVERED")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Order o WHERE o.restaurant.id = :restaurantId AND o.status = com.foodwings.enums.OrderStatus.DELIVERED")
    BigDecimal calculateRevenueByRestaurant(Long restaurantId);
}
