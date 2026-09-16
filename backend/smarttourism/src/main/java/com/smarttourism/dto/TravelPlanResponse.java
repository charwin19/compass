package com.smarttourism.dto;

import com.smarttourism.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/** Complete travel plan response with all recommendations. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelPlanResponse {

    private Long          id;
    private String        destination;
    private Integer       budget;
    private Integer       numberOfDays;
    private Integer       numberOfTravelers;
    private String        travelType;
    private String        travelStyle;
    private String        status;
    private LocalDateTime createdAt;

    // ---- Recommendations ----
    private DestinationInfo         destinationInfo;
    private List<PlaceInfo>         touristPlaces;
    private List<HiddenPlaceInfo>   hiddenPlaces;
    private List<HotelInfo>         hotels;
    private List<GuideInfo>         guides;
    private RouteInfo               route;
    private List<ItineraryDayGroup> itinerary;

    // ---- Budget summary ----
    private BudgetSummary budgetSummary;

    // ---- Nested DTOs ----

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DestinationInfo {
        private Long   id;
        private String name;
        private String state;
        private String description;
        private String imageUrl;
        private Double rating;
        private String category;
        private String bestTimeToVisit;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class PlaceInfo {
        private Long   id;
        private String name;
        private String description;
        private String imageUrl;
        private String location;
        private Double visitingTimeHours;
        private Integer entryFee;
        private String openTime;
        private String closeTime;
        private String budgetCategory;
        private String tags;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class HiddenPlaceInfo {
        private Long   id;
        private String name;
        private String location;
        private String description;
        private String whyVisit;
        private String bestTimeToVisit;
        private Double visitingTimeHours;
        private String imageUrl;
        private String difficultyLevel;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class HotelInfo {
        private Long    id;
        private String  name;
        private String  description;
        private String  imageUrl;
        private Double  rating;
        private Integer reviewCount;
        private Integer pricePerNight;
        private Double  distanceFromCenter;
        private String  budgetType;
        private String  facilities;
        private String  address;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class GuideInfo {
        private Long    id;
        private String  name;
        private String  bio;
        private String  photoUrl;
        private Integer experienceYears;
        private String  languages;
        private Double  rating;
        private Integer reviewCount;
        private String  contactMasked;
        private Integer pricePerDay;
        private String  specialization;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class RouteInfo {
        private Long    id;
        private String  startLocation;
        private String  endLocation;
        private Double  distanceKm;
        private Double  estimatedTimeHours;
        private String  roadType;
        private String  roadCondition;
        private String  importantStops;
        private String  alternativeRoute;
        private String  travelMode;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ItineraryDayGroup {
        private Integer       dayNumber;
        private List<ActivityInfo> activities;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ActivityInfo {
        private String  placeName;
        private String  description;
        private String  startTime;
        private String  endTime;
        private String  activityType;
        private Integer estimatedCost;
        private String  notes;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class BudgetSummary {
        private Integer totalBudget;
        private Integer estimatedAccommodation;
        private Integer estimatedFood;
        private Integer estimatedTransport;
        private Integer estimatedActivities;
        private Integer estimatedTotal;
        private Integer remainingBuffer;
        private String  travelStyle;
    }
}
