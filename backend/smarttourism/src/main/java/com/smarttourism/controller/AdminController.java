package com.smarttourism.controller;

import com.smarttourism.dto.EmergencyResponse;
import com.smarttourism.service.EmergencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Admin-only endpoints.
 * All routes require ROLE_ADMIN (enforced both by SecurityConfig and @PreAuthorize).
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final EmergencyService emergencyService;

    /**
     * GET /api/admin/emergency
     * List all emergency alerts ordered by newest first.
     */
    @GetMapping("/emergency")
    public ResponseEntity<List<EmergencyResponse>> getAllAlerts() {
        return ResponseEntity.ok(emergencyService.getAllAlerts());
    }

    /**
     * PATCH /api/admin/emergency/{id}/status
     * Update the status of an emergency alert.
     *
     * Body: { "status": "RESPONDING", "notes": "Dispatch team en route" }
     */
    @PatchMapping("/emergency/{id}/status")
    public ResponseEntity<EmergencyResponse> updateAlertStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String status = body.get("status");
        String notes  = body.get("notes");

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status field is required");
        }

        return ResponseEntity.ok(emergencyService.updateStatus(id, status, notes));
    }
}
