// FILE: src/main/java/com/workshop/dto/ProfileUpdateRequest.java
package com.workshop.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProfileUpdateRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phone;

    @NotBlank(message = "Department is required")
    private String department;

    @Size(max = 50, message = "Roll number must not exceed 50 characters")
    private String rollNumber;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Size(max = 100, message = "HOD contact must not exceed 100 characters")
    private String hodContact;
}
