// FILE: src/main/java/com/workshop/dto/UserResponse.java
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
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String role;
    private String phone;
    private String department;
    private String rollNumber;
    private Boolean externalUser;
    private LocalDateTime createdAt;
}
