package com.smarttourism.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * An emergency alert sent by a traveler via the Emergency button.
 *
 * IMPORTANT: This system is for demonstration/project purposes.
 * For actual emergency services, an authorized integration with
 * official emergency response systems (e.g., 112) must be configured.
 */
@Entity
@Table(name = "emergency_alerts")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmergencyAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** GPS latitude of the user at the time of the alert */
    private Double latitude;

    /** GPS longitude of the user at the time of the alert */
    private Double longitude;

    @Column(length = 150)
    private String destination;

    /** Type: MEDICAL / ACCIDENT / SAFETY / LOST / OTHER */
    @Column(length = 30)
    private String emergencyType;

    /** Additional message from the user */
    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private AlertStatus status = AlertStatus.NEW;

    /** Admin notes when updating status */
    @Column(columnDefinition = "TEXT")
    private String adminNotes;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Optionally linked to a travel plan */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_plan_id")
    private TravelPlan travelPlan;

    public enum AlertStatus { NEW, ACKNOWLEDGED, RESPONDING, RESOLVED }
}
