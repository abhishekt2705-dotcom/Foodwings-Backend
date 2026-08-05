package com.foodwings.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A favorite restaurant saved by a customer.
 */
@Entity
@Table(name = "wishlists", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "restaurant_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wishlist extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    // Explicit setters/getters for IDEs that may not recognise Lombok
    public void setUser(User user) { this.user = user; }
    public User getUser() { return this.user; }

    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }
    public Restaurant getRestaurant() { return this.restaurant; }
}
