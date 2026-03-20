// FILE: src/main/java/com/workshop/dto/AdminStatsResponse.java
package com.workshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsResponse {
    private long totalWorkshops;
    private long totalStudents;
    private long totalRegistrations;
    private BigDecimal totalRevenue;
    private long pendingPayments;
    private long confirmedRegistrations;
}
