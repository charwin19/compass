package com.smarttourism.service;

import com.smarttourism.dto.AuthResponse;
import com.smarttourism.dto.LoginRequest;
import com.smarttourism.dto.RegisterRequest;
import com.smarttourism.entity.User;
import com.smarttourism.entity.User.Role;
import com.smarttourism.exception.ResourceNotFoundException;
import com.smarttourism.repository.UserRepository;
import com.smarttourism.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles user registration and login with JWT token generation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository   userRepository;
    private final PasswordEncoder  passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Register a new user.
     *
     * @throws IllegalArgumentException if email/phone already registered, or passwords don't match
     */
    @Transactional
    public AuthResponse register(RegisterRequest req) {

        // Validate password confirmation if provided
        if (req.getConfirmPassword() != null && !req.getConfirmPassword().isBlank()) {
            if (!req.getPassword().equals(req.getConfirmPassword())) {
                throw new IllegalArgumentException("Passwords do not match.");
            }
        }

        // Check duplicate email
        if (userRepository.existsByEmail(req.getEmail().toLowerCase())) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        // Check duplicate phone (if provided)
        if (req.getPhone() != null && !req.getPhone().isBlank()) {
            if (userRepository.existsByPhone(req.getPhone())) {
                throw new IllegalArgumentException("An account with this phone number already exists.");
            }
        }

        // Build and save user with hashed password
        User user = User.builder()
                .fullName(req.getFullName().trim())
                .email(req.getEmail().toLowerCase().trim())
                .phone(req.getPhone())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(Role.USER)
                .build();

        user = userRepository.save(user);
        log.info("New user registered: {} (id={})", user.getEmail(), user.getId());

        // Generate JWT
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .message("Registration successful! Welcome to Compass.")
                .build();
    }

    /**
     * Login an existing user.
     *
     * @throws BadCredentialsException if credentials are invalid
     */
    public AuthResponse login(LoginRequest req) {

        // Find user by email (safe — does not expose whether email exists or not in error message)
        User user = userRepository.findByEmail(req.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password."));

        // Verify BCrypt password
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            log.warn("Failed login attempt for email: {}", req.getEmail());
            throw new BadCredentialsException("Invalid email or password.");
        }

        log.info("User logged in: {} (id={})", user.getEmail(), user.getId());

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .message("Login successful!")
                .build();
    }

    /**
     * Retrieve the currently authenticated user's profile.
     */
    @Transactional(readOnly = true)
    public User getProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
