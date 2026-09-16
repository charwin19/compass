package com.smarttourism.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/** Emergency alert request sent by traveler. */
@Data
public class EmergencyRequest {

    /** GPS latitude — nullable if permission denied */
    private Double latitude;

    /** GPS longitude — nullable if permission denied */
    private Double longitude;

    @Size(max = 150, message = "Destination name too long")
    private String destination;

    @NotBlank(message = "Emergency type is required")
    private String emergencyType; // MEDICAL / ACCIDENT / SAFETY / LOST / OTHER

    @Size(max = 1000, message = "Message too long")
    private String message;

    /** Optional: link to a travel plan */
    private Long travelPlanId;
}
