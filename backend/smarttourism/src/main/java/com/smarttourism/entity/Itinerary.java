package com.smarttourism.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * A single time-slot activity in a day-wise travel itinerary.
 */
@Entity
@Table(name = "itineraries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Itinerary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Day number in the trip (1, 2, 3, ...) */
    @Column(nullable = false)
    private Integer dayNumber;

    /** Start time as string, e.g. "08:00 AM" */
    @Column(length = 20)
    private String startTime;

    /** End time as string, e.g. "09:30 AM" */
    @Column(length = 20)
    private String endTime;

    /** Activity name, e.g. "Visit Botanical Garden" */
    @Column(nullable = false, length = 200)
    private String activity;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** Activity type: SIGHTSEEING / MEAL / TRAVEL / REST / HIDDEN_GEM */
    @Column(length = 30)
    private String activityType;

    /** Estimated cost in INR for this activity */
    private Integer estimatedCost;

    /** Location of this activity */
    @Column(length = 150)
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_plan_id", nullable = false)
    private TravelPlan travelPlan;
}
