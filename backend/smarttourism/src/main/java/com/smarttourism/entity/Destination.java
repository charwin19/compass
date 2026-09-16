package com.smarttourism.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Represents a tourist destination (e.g., Ooty, Munnar).
 */
@Entity
@Table(name = "destinations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 100)
    private String state;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String imageUrl;

    /** Average rating (1–5) */
    private Double rating;

    private Integer reviewCount;

    /** Destination category (Hill Station, Coastal, Heritage, etc.) */
    @Column(length = 50)
    private String category;

    /** Best months to visit */
    @Column(length = 100)
    private String bestTimeToVisit;

    // ---- Relationships ----

    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Place> places;

    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HiddenPlace> hiddenPlaces;

    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Guide> guides;

    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Hotel> hotels;

    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Route> routes;
}
