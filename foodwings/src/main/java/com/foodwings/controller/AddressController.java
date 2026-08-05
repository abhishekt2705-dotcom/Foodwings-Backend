package com.foodwings.controller;

import com.foodwings.dto.request.AddressRequest;
import com.foodwings.dto.response.AddressResponse;
import com.foodwings.response.ApiResponse;
import com.foodwings.security.CurrentUserService;
import com.foodwings.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;
    private final CurrentUserService currentUserService;

    public AddressController(AddressService addressService,
                             CurrentUserService currentUserService) {
        this.addressService = addressService;
        this.currentUserService = currentUserService;
    }

    private Long userId() {
        return currentUserService.getCurrentUserId();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getMyAddresses() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Addresses fetched successfully",
                        addressService.getMyAddresses(userId())
                )
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            @Valid @RequestBody AddressRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Address added successfully",
                        addressService.addAddress(userId(), request)
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddressById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Address fetched successfully",
                        addressService.getAddressById(userId(), id)
                )
        );
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAddress(
            @PathVariable Long id) {

        addressService.deleteAddress(userId(), id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Address deleted successfully",
                        "Deleted"
                )
        );
    }

    @PutMapping("/{id}/default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Default address updated",
                        addressService.setDefaultAddress(userId(), id)
                )
        );
    }
}