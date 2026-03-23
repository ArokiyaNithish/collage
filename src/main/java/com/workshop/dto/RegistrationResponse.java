// FILE: src/main/java/com/workshop/dto/RegistrationResponse.java
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
public class RegistrationResponse {
    private Long registrationId;
    private Long workshopId;
    private String workshopTitle;
    private String instructor;
    private String venue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal fee;
    private String category;
    private LocalDateTime registeredAt;
    private Integer teamMembersCount;
    private String teamMembers;
    private Boolean attended;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String registrationStatus;
    private String paymentStatus;    // null if no payment yet
    private String transactionId;    // null if no payment yet
}
