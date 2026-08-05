package com.foodwings.entity;

import com.foodwings.enums.FoodType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * A menu item offered by a restaurant.
 */
@Entity
@Table(name = "food_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodItem extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private FoodType foodType = FoodType.VEG;

    private String imagePath;

    @Column(nullable = false)
    @Builder.Default
    private boolean available = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean bestSeller = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean popular = false;

    @Column(nullable = false)
    @Builder.Default
    private double rating = 0.0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
    
    // Explicit setters/getters to help IDEs without Lombok
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }
    public void setFoodType(FoodType foodType) { this.foodType = foodType; }
    public void setAvailable(boolean available) { this.available = available; }
    public void setBestSeller(boolean bestSeller) { this.bestSeller = bestSeller; }
    public void setPopular(boolean popular) { this.popular = popular; }
    public void setRating(double rating) { this.rating = rating; }
    public void setCategory(Category category) { this.category = category; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }
}
