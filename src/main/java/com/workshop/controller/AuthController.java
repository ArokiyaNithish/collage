// FILE: src/main/java/com/workshop/controller/AuthController.java
package com.workshop.controller;

import com.workshop.dto.*;
import com.workshop.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        Map<String, Object> response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("🔐 Login attempt for email: {}", request.getEmail());
        try {
            AuthResponse response = authService.login(request);
            log.info("✅ Login successful for: {} (Role: {})", request.getEmail(), response.getRole());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ Login failed for: {}. Error: {}", request.getEmail(), e.getMessage());
            throw e;
        }
    }

    @GetMapping("/students")
    public ResponseEntity<List<StudentSummaryResponse>> getAllStudents() {
        return ResponseEntity.ok(authService.getAllStudents());
    }
}
