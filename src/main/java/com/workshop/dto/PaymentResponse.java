// FILE: src/main/java/com/workshop/dto/PaymentResponse.java
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
public class PaymentResponse {
    private Long paymentId;
    private Long registrationId;
    private Long studentId;
    private String studentName;
    private Long workshopId;
    private String workshopTitle;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentStatus;
    private String transactionId;
    private String cardLast4;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;

    // Additional Student Info
    private String studentEmail;
    private String studentPhone;
    private String studentDepartment;
    private String studentRollNumber;
}
