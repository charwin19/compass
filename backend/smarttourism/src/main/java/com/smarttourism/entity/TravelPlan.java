package com.smarttourism.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A personalized travel plan generated for a user.
 */
@Entity
@Table(name = "travel_plans")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TravelPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String destination;

    /** Budget in INR */
    private Integer budget;

    private Integer numberOfDays;

    private Integer numberOfTravelers;

    /** SOLO / FAMILY / FRIENDS / COUPLE */
    @Column(length = 20)
    private String travelType;

    /** BUDGET / STANDARD / LUXURY */
    @Column(length = 20)
    private String travelStyle;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private Status status = Status.PLANNED;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Day-wise itinerary items */
    @OneToMany(mappedBy = "travelPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("dayNumber ASC, startTime ASC")
    private List<Itinerary> itineraries;

    public enum Status { PLANNED, ACTIVE, COMPLETED, CANCELLED }
}
