package com.smarttourism.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * A tourist attraction / place within a destination.
 */
@Entity
@Table(name = "places")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String imageUrl;

    @Column(length = 150)
    private String location;

    /** Estimated visiting time in hours */
    private Double visitingTimeHours;

    /** Entry fee in INR (0 = free) */
    private Integer entryFee;

    /** Suggested day number in the itinerary */
    private Integer recommendedDay;

    /** Opening time, e.g. "09:00" */
    @Column(length = 10)
    private String openTime;

    /** Closing time, e.g. "18:00" */
    @Column(length = 10)
    private String closeTime;

    /** Budget category: BUDGET / STANDARD / LUXURY */
    @Column(length = 20)
    private String budgetCategory;

    /** Tags: "family, solo, couple, adventure" */
    @Column(length = 200)
    private String tags;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id", nullable = false)
    private Destination destination;
}
