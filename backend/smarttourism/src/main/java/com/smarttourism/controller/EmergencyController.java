package com.smarttourism.controller;

import com.smarttourism.dto.EmergencyRequest;
import com.smarttourism.dto.EmergencyResponse;
import com.smarttourism.service.EmergencyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Emergency alert endpoints for authenticated travelers.
 */
@RestController
@RequestMapping("/api/emergency")
@RequiredArgsConstructor
public class EmergencyController {

    private final EmergencyService emergencyService;

    /**
     * POST /api/emergency
     * Submit an emergency alert with GPS location.
     */
    @PostMapping
    public ResponseEntity<EmergencyResponse> createAlert(
            @Valid @RequestBody EmergencyRequest req,
            @AuthenticationPrincipal Long userId) {

        EmergencyResponse response = emergencyService.createAlert(req, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/emergency/my-alerts
     * Get the authenticated user's own emergency history.
     */
    @GetMapping("/my-alerts")
    public ResponseEntity<List<EmergencyResponse>> getMyAlerts(
            @AuthenticationPrincipal Long userId) {

        return ResponseEntity.ok(emergencyService.getMyAlerts(userId));
    }
}
