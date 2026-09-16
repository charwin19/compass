package com.smarttourism.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * A travel route from one location to a destination.
 */
@Entity
@Table(name = "routes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String startLocation;

    @Column(nullable = false, length = 150)
    private String endLocation;

    /** Distance in kilometres */
    private Double distanceKm;

    /** Estimated travel time in hours */
    private Double estimatedTimeHours;

    /** Road type: NH (National Highway), SH (State Highway), Mountain Road */
    @Column(length = 50)
    private String roadType;

    /** Overall road condition: GOOD / AVERAGE / POOR */
    @Column(length = 20)
    private String roadCondition;

    /** Important stops along the route (comma-separated) */
    @Column(columnDefinition = "TEXT")
    private String importantStops;

    /** Alternative route description */
    @Column(columnDefinition = "TEXT")
    private String alternativeRoute;

    /** Travel mode: ROAD / TRAIN / AIR */
    @Column(length = 20)
    private String travelMode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id", nullable = false)
    private Destination destination;
}
