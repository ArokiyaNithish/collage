// FILE: src/main/java/com/workshop/dto/UserProfileResponse.java
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
public class UserProfileResponse {
    private Long id;
    private String fullName;
    private String email;
    private String role;
    private String phone;
    private String department;
    private String rollNumber;
    private String address;
    private String hodContact;
    private java.math.BigDecimal totalAmountSpent;
    private LocalDateTime createdAt;
}
