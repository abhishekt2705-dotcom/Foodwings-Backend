package com.foodwings.controller;

import com.foodwings.dto.request.AddressRequest;
import com.foodwings.dto.request.UpdateProfileRequest;
import com.foodwings.dto.response.AddressResponse;
import com.foodwings.dto.response.UserResponse;
import com.foodwings.response.ApiResponse;
import com.foodwings.security.CurrentUserService;
import com.foodwings.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "Profile", description = "Current user's profile, photo and addresses")
public class ProfileController {

    private final UserService userService;
    private final CurrentUserService currentUserService;

    public ProfileController(UserService userService, CurrentUserService currentUserService) {
        this.userService = userService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    @Operation(summary = "Get the current user's profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile() {
        return ResponseEntity.ok(ApiResponse.success("Profile fetched",
                userService.getProfile(currentUserService.getCurrentUserId())));
    }

    @PutMapping
    @Operation(summary = "Update the current user's profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated",
                userService.updateProfile(currentUserService.getCurrentUserId(), request)));
    }

    @PostMapping("/photo")
    @Operation(summary = "Upload the current user's profile photo")
    public ResponseEntity<ApiResponse<UserResponse>> uploadPhoto(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.success("Profile photo uploaded",
                userService.uploadProfilePhoto(currentUserService.getCurrentUserId(), file)));
    }

    @GetMapping("/addresses")
    @Operation(summary = "List the current user's addresses")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses() {
        return ResponseEntity.ok(ApiResponse.success("Addresses fetched",
                userService.getAddresses(currentUserService.getCurrentUserId())));
    }

    @PostMapping("/addresses")
    @Operation(summary = "Add a new address")
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(@Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Address added",
                userService.addAddress(currentUserService.getCurrentUserId(), request)));
    }

    @PutMapping("/addresses/{id}")
    @Operation(summary = "Update an address")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(@PathVariable Long id,
                                                                      @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Address updated",
                userService.updateAddress(currentUserService.getCurrentUserId(), id, request)));
    }

    @DeleteMapping("/addresses/{id}")
    @Operation(summary = "Delete an address")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Long id) {
        userService.deleteAddress(currentUserService.getCurrentUserId(), id);
        return ResponseEntity.ok(ApiResponse.success("Address deleted"));
    }
}
