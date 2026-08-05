package com.foodwings.service.impl;

import com.foodwings.dto.request.ChangePasswordRequest;
import com.foodwings.dto.request.ForgotPasswordRequest;
import com.foodwings.dto.request.LoginRequest;
import com.foodwings.dto.request.RefreshTokenRequest;
import com.foodwings.dto.request.RegisterRequest;
import com.foodwings.dto.request.ResetPasswordRequest;
import com.foodwings.dto.response.AuthResponse;
import com.foodwings.entity.DeliveryPartner;
import com.foodwings.entity.PasswordResetToken;
import com.foodwings.entity.RefreshToken;
import com.foodwings.entity.Role;
import com.foodwings.entity.User;
import com.foodwings.enums.RoleName;
import com.foodwings.exception.BadRequestException;
import com.foodwings.exception.DuplicateResourceException;
import com.foodwings.exception.ResourceNotFoundException;
import com.foodwings.exception.UnauthorizedException;
import com.foodwings.mapper.UserMapper;
import com.foodwings.repository.DeliveryPartnerRepository;
import com.foodwings.repository.PasswordResetTokenRepository;
import com.foodwings.repository.RefreshTokenRepository;
import com.foodwings.repository.RoleRepository;
import com.foodwings.repository.UserRepository;
import com.foodwings.security.CustomUserDetails;
import com.foodwings.service.AuthService;
import com.foodwings.util.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final DeliveryPartnerRepository deliveryPartnerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           PasswordResetTokenRepository passwordResetTokenRepository,
                           DeliveryPartnerRepository deliveryPartnerRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.deliveryPartnerRepository = deliveryPartnerRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already registered: " + request.getEmail());
        }
        Role role = roleRepository.findByName(request.getRole())
                .orElseGet(() -> roleRepository.save(new Role(request.getRole())));

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .active(true)
                .build();
        user.addRole(role);
        User saved = userRepository.save(user);

        if (request.getRole() == RoleName.DELIVERY_PARTNER) {
            deliveryPartnerRepository.save(DeliveryPartner.builder()
                    .user(saved)
                    .available(true)
                    .totalDeliveries(0)
                    .build());
        }

        return buildAuthResponse(saved);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
        User user = userRepository.findById(details.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", details.getId()));
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));
        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("Refresh token has expired, please login again");
        }
        User user = refreshToken.getUser();
        String accessToken = tokenProvider.generateAccessToken(user.getEmail(), roleNames(user));
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .user(UserMapper.toResponse(user))
                .build();
    }

    @Override
    public void logout(RefreshTokenRequest request) {
        refreshTokenRepository.findByToken(request.getRefreshToken())
                .ifPresent(refreshTokenRepository::delete);
    }

    @Override
    public String forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(Instant.now().plusSeconds(3600))
                .used(false)
                .build();
        passwordResetTokenRepository.save(resetToken);
        return token;
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid reset token"));
        if (resetToken.isUsed()) {
            throw new BadRequestException("Reset token has already been used");
        }
        if (resetToken.isExpired()) {
            throw new BadRequestException("Reset token has expired");
        }
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        List<String> roles = roleNames(user);
        String accessToken = tokenProvider.generateAccessToken(user.getEmail(), roles);
        String refreshToken = createRefreshToken(user);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(UserMapper.toResponse(user))
                .build();
    }

    private String createRefreshToken(User user) {
        // Enforce one active refresh token per user (unique user_id constraint)
        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.flush();
        RefreshToken token = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plusMillis(tokenProvider.getRefreshTokenExpirationMs()))
                .build();
        return refreshTokenRepository.save(token).getToken();
    }

    private List<String> roleNames(User user) {
        return user.getRoles().stream().map(Role::getName).map(RoleName::name).toList();
    }
}
