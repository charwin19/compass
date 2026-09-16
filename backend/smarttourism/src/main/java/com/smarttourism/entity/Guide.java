package com.smarttourism.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * A verified local tour guide associated with a destination.
 */
@Entity
@Table(name = "guides")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Guide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 255)
    private String photoUrl;

    /** Years of guiding experience */
    private Integer experienceYears;

    /** Comma-separated languages, e.g. "Tamil, English, Hindi" */
    @Column(length = 200)
    private String languages;

    /** Rating out of 5 */
    private Double rating;

    private Integer reviewCount;

    /**
     * Obfuscated contact — in production, contact is routed through the platform.
     * For demo, store a masked phone like "XXXXXX1234".
     */
    @Column(length = 20)
    private String contactMasked;

    /** Price per day in INR */
    private Integer pricePerDay;

    /** Specialization: "Wildlife, Heritage, Adventure, Cultural" */
    @Column(length = 150)
    private String specialization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id", nullable = false)
    private Destination destination;
}
