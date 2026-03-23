// FILE: src/main/java/com/workshop/model/User.java
package com.workshop.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String department;

    @Column(name = "roll_number", length = 50)
    private String rollNumber;

    @Column(length = 500)
    private String address;

    @Column(name = "hod_contact", length = 100)
    private String hodContact;

    @Builder.Default
    @Column(name = "external_user")
    private Boolean externalUser = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
