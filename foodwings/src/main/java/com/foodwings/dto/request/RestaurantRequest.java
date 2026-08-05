package com.foodwings.dto.request;

import com.foodwings.validation.Phone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class RestaurantRequest {

    @NotBlank(message = "Restaurant name is required")
    @Size(max = 120)
    private String name;

    @Size(max = 500)
    private String description;

    @NotBlank(message = "Address is required")
    private String address;

    private String city;

    @Phone
    private String phone;

    @Email(message = "Email must be valid")
    private String email;

    private LocalTime openingTime;

    private LocalTime closingTime;
}
