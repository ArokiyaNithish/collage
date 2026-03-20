// FILE: src/main/java/com/workshop/model/Workshop.java
package com.workshop.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "workshops")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Workshop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(nullable = false, length = 100)
    private String instructor;

    @Column(nullable = false, length = 200)
    private String venue;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "team_size", nullable = false)
    @Builder.Default
    private Integer teamSize = 1;

    @Column(name = "seats_available", nullable = false)
    private Integer seatsAvailable;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal fee;

    @Column(length = 100)
    private String category;

    @Column(name = "coordinator_name", length = 100)
    private String coordinatorName;

    @Column(name = "coordinator_phone", length = 20)
    private String coordinatorPhone;

    @Column(name = "coordinator_email", length = 100)
    private String coordinatorEmail;

    @Column(length = 20)
    private String status;

    @Column(name = "created_by")
    private Long createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
