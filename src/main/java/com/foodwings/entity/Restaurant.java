package com.foodwings.entity;

import com.foodwings.enums.RestaurantStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A restaurant owned by a RESTAURANT_OWNER. Approved by an ADMIN before it becomes visible.
 */
@Entity
@Table(name = "restaurants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String address;

    @Column(length = 60)
    private String city;

    @Column(length = 15)
    private String phone;

    @Column(length = 120)
    private String email;

    private String logo;

    private String banner;

    private LocalTime openingTime;

    private LocalTime closingTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private RestaurantStatus status = RestaurantStatus.PENDING;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(nullable = false)
    @Builder.Default
    private double rating = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private int totalReviews = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FoodItem> foods = new ArrayList<>();
    
    // Explicit setters/getters to help IDEs without Lombok
    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setAddress(String address) { this.address = address; }
    public void setCity(String city) { this.city = city; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setOpeningTime(LocalTime openingTime) { this.openingTime = openingTime; }
    public void setClosingTime(LocalTime closingTime) { this.closingTime = closingTime; }
    public void setStatus(RestaurantStatus status) { this.status = status; }
    public void setActive(boolean active) { this.active = active; }
    public void setRating(double rating) { this.rating = rating; }
    public void setOwner(User owner) { this.owner = owner; }
}
