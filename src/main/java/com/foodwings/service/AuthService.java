package com.foodwings.service;

import com.foodwings.dto.request.ChangePasswordRequest;
import com.foodwings.dto.request.ForgotPasswordRequest;
import com.foodwings.dto.request.LoginRequest;
import com.foodwings.dto.request.RefreshTokenRequest;
import com.foodwings.dto.request.RegisterRequest;
import com.foodwings.dto.request.ResetPasswordRequest;
import com.foodwings.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshTokenRequest request);

    void logout(RefreshTokenRequest request);

    /**
     * Generates a one-time password-reset token. In a real system this would be
     * emailed to the user; here it is returned so the flow can be exercised end-to-end.
     */
    String forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void changePassword(Long userId, ChangePasswordRequest request);
}
