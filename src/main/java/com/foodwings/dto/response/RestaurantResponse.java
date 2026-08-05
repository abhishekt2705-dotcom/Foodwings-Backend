package com.foodwings.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantResponse {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String city;
    private String phone;
    private String email;
    private String logo;
    private String banner;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private String status;
    private boolean active;
    private double rating;
    private int totalReviews;
    private Long ownerId;
    private String ownerName;
}
