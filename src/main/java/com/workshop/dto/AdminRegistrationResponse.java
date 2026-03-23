// FILE: src/main/java/com/workshop/dto/AdminRegistrationResponse.java
package com.workshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminRegistrationResponse {
    private Long registrationId;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private Long workshopId;
    private String workshopTitle;
    private LocalDateTime registeredAt;
    private String registrationStatus;
    private String paymentStatus;
    private String transactionId;
    private Integer teamMembersCount;
    private String teamMembers;
    private Boolean attended;
    private java.time.LocalDateTime checkInTime;
    private java.time.LocalDateTime checkOutTime;
    
    // Student Details
    private String studentPhone;
    private String studentDepartment;
    private String studentRollNumber;
    
    // Coordinator Details
    private String coordinatorName;
    private String coordinatorPhone;
}
