package com.foodwings.dto.response;

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
public class AddressResponse {
    private Long id;
    private String label;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String pincode;
    private String phone;
    private boolean defaultAddress;
}
