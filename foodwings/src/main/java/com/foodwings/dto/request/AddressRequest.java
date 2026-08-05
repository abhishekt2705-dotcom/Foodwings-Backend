package com.foodwings.dto.request;

import com.foodwings.validation.Phone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequest {

    private String label;

    @NotBlank(message = "Address line 1 is required")
    private String line1;

    private String line2;

    @NotBlank(message = "City is required")
    private String city;

    private String state;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[0-9]{4,10}$", message = "Pincode must be 4-10 digits")
    private String pincode;

    @Phone
    private String phone;

    private boolean defaultAddress;
}
