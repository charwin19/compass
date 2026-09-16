package com.smarttourism.service;

import com.smarttourism.dto.EmergencyRequest;
import com.smarttourism.dto.EmergencyResponse;
import com.smarttourism.entity.EmergencyAlert;
import com.smarttourism.entity.EmergencyAlert.AlertStatus;
import com.smarttourism.entity.TravelPlan;
import com.smarttourism.entity.User;
import com.smarttourism.exception.ResourceNotFoundException;
import com.smarttourism.repository.EmergencyRepository;
import com.smarttourism.repository.TravelPlanRepository;
import com.smarttourism.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles emergency alert creation and status management.
 *
 * NOTE: This is a DEMONSTRATION system. Real emergency services
 * should integrate with official 112/ambulance APIs.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmergencyService {

    private final EmergencyRepository  emergencyRepo;
    private final UserRepository       userRepo;
    private final TravelPlanRepository travelPlanRepo;

    /**
     * Create a new emergency alert.
     */
    @Transactional
    public EmergencyResponse createAlert(EmergencyRequest req, Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate emergency type
        String type = req.getEmergencyType().toUpperCase().trim();
        if (!List.of("MEDICAL", "ACCIDENT", "SAFETY", "LOST", "OTHER").contains(type)) {
            throw new IllegalArgumentException(
                    "Invalid emergency type. Use: MEDICAL, ACCIDENT, SAFETY, LOST, OTHER");
        }

        // Optionally link to travel plan
        TravelPlan travelPlan = null;
        if (req.getTravelPlanId() != null) {
            travelPlan = travelPlanRepo.findById(req.getTravelPlanId()).orElse(null);
        }

        EmergencyAlert alert = EmergencyAlert.builder()
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .destination(req.getDestination())
                .emergencyType(type)
                .message(req.getMessage())
                .status(AlertStatus.NEW)
                .user(user)
                .travelPlan(travelPlan)
                .build();

        alert = emergencyRepo.save(alert);

        log.warn("[EMERGENCY ALERT] id={} type={} user={} lat={} lng={}",
                alert.getId(), type, user.getEmail(),
                req.getLatitude(), req.getLongitude());

        return mapToResponse(alert);
    }

    /**
     * Get all alerts belonging to the current user.
     */
    @Transactional(readOnly = true)
    public List<EmergencyResponse> getMyAlerts(Long userId) {
        return emergencyRepo.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all alerts (admin only).
     */
    @Transactional(readOnly = true)
    public List<EmergencyResponse> getAllAlerts() {
        return emergencyRepo.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update alert status (admin only).
     */
    @Transactional
    public EmergencyResponse updateStatus(Long alertId, String newStatus, String adminNotes) {
        EmergencyAlert alert = emergencyRepo.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Emergency alert not found: " + alertId));

        AlertStatus status;
        try {
            status = AlertStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Invalid status. Use: NEW, ACKNOWLEDGED, RESPONDING, RESOLVED");
        }

        alert.setStatus(status);
        if (adminNotes != null) alert.setAdminNotes(adminNotes);
        if (status == AlertStatus.RESOLVED) alert.setResolvedAt(LocalDateTime.now());

        alert = emergencyRepo.save(alert);
        log.info("Emergency alert {} status updated to {}", alertId, status);

        return mapToResponse(alert);
    }

    // ---- mapper ----
    private EmergencyResponse mapToResponse(EmergencyAlert a) {
        return EmergencyResponse.builder()
                .id(a.getId())
                .latitude(a.getLatitude())
                .longitude(a.getLongitude())
                .destination(a.getDestination())
                .emergencyType(a.getEmergencyType())
                .message(a.getMessage())
                .status(a.getStatus() != null ? a.getStatus().name() : null)
                .adminNotes(a.getAdminNotes())
                .createdAt(a.getCreatedAt())
                .resolvedAt(a.getResolvedAt())
                .userId(a.getUser().getId())
                .userName(a.getUser().getFullName())
                .userEmail(a.getUser().getEmail())
                .build();
    }
}
