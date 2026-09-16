package com.smarttourism.controller;

import com.smarttourism.dto.AuthResponse;
import com.smarttourism.dto.LoginRequest;
import com.smarttourism.dto.RegisterRequest;
import com.smarttourism.entity.User;
import com.smarttourism.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Authentication endpoints — register, login, profile.
 * All endpoints in /api/auth/** are public.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     * Register a new user account.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        AuthResponse response = authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/auth/login
     * Authenticate an existing user.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        AuthResponse response = authService.login(req);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/auth/profile
     * Returns the authenticated user's profile (requires JWT).
     * The userId comes from the JWT filter via @AuthenticationPrincipal.
     */
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> profile(@AuthenticationPrincipal Long userId) {
        User user = authService.getProfile(userId);
        return ResponseEntity.ok(Map.of(
                "id",        user.getId(),
                "fullName",  user.getFullName(),
                "email",     user.getEmail(),
                "phone",     user.getPhone() != null ? user.getPhone() : "",
                "role",      user.getRole().name(),
                "createdAt", user.getCreatedAt() != null ? user.getCreatedAt().toString() : ""
        ));
    }
}
