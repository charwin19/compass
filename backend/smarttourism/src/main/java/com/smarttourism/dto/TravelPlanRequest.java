package com.smarttourism.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/** Request to generate a personalised travel plan. */
@Data
public class TravelPlanRequest {

    @NotBlank(message = "Destination is required")
    @Size(max = 150, message = "Destination name too long")
    private String destination;

    @NotNull(message = "Budget is required")
    @Min(value = 500, message = "Minimum budget is ₹500")
    @Max(value = 10_000_000, message = "Budget seems unreasonably large")
    private Integer budget;

    @NotNull(message = "Number of days is required")
    @Min(value = 1, message = "Minimum 1 day")
    @Max(value = 30, message = "Maximum 30 days supported")
    private Integer numberOfDays;

    @Min(value = 1, message = "At least 1 traveler")
    @Max(value = 50, message = "Maximum 50 travelers")
    private Integer numberOfTravelers = 1;

    /** SOLO / FAMILY / FRIENDS / COUPLE */
    private String travelType = "SOLO";
}
