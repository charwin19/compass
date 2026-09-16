package com.smarttourism.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Response returned after creating or fetching an emergency alert. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyResponse {

    private Long   id;
    private Double latitude;
    private Double longitude;
    private String destination;
    private String emergencyType;
    private String message;
    private String status;
    private String adminNotes;

    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    // User info (limited — no sensitive data)
    private Long   userId;
    private String userName;
    private String userEmail;
}
