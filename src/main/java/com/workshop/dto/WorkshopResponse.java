// FILE: src/main/java/com/workshop/dto/WorkshopResponse.java
package com.workshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkshopResponse {
    private Long id;
    private String title;
    private String description;
    private String instructor;
    private String venue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer capacity;
    private Integer teamSize;
    private Integer seatsAvailable;
    private BigDecimal fee;
    private String category;
    private String coordinatorName;
    private String coordinatorPhone;
    private String coordinatorEmail;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
