// FILE: src/main/java/com/workshop/dto/StudentSummaryResponse.java
package com.workshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentSummaryResponse {
    private Long id;
    private String fullName;
    private String department;
    private String email;
    private String rollNumber;
}
