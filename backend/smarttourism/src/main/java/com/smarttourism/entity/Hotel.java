package com.smarttourism.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * A hotel recommendation within a destination.
 */
@Entity
@Table(name = "hotels")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String imageUrl;

    /** Rating out of 5 */
    private Double rating;

    private Integer reviewCount;

    /** Price per night in INR */
    private Integer pricePerNight;

    /** Distance from the main tourist area in km */
    private Double distanceFromCenter;

    /** Budget type: BUDGET / STANDARD / LUXURY */
    @Column(length = 20)
    private String budgetType;

    /**
     * Comma-separated amenities, e.g.
     * "WiFi, AC, Parking, Restaurant, Swimming Pool"
     */
    @Column(length = 300)
    private String facilities;

    @Column(length = 200)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id", nullable = false)
    private Destination destination;
}
