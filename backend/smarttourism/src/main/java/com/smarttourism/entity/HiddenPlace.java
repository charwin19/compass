package com.smarttourism.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * A lesser-known / hidden tourist gem within a destination.
 * This is a key differentiating feature of the platform.
 */
@Entity
@Table(name = "hidden_places")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HiddenPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 150)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** Why visit this hidden place? */
    @Column(columnDefinition = "TEXT")
    private String whyVisit;

    /** Best months/time of day to visit */
    @Column(length = 100)
    private String bestTimeToVisit;

    /** Estimated visiting duration in hours */
    private Double visitingTimeHours;

    @Column(length = 255)
    private String imageUrl;

    /** Difficulty level: EASY / MODERATE / HARD */
    @Column(length = 20)
    private String difficultyLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id", nullable = false)
    private Destination destination;
}
