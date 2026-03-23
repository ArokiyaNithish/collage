// FILE: src/main/java/com/workshop/controller/StudentController.java
package com.workshop.controller;

import com.workshop.dto.*;
import com.workshop.service.PaymentService;
import com.workshop.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.workshop.repository.UserRepository;
import com.workshop.exception.UserNotFoundException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;
    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public StudentController(StudentService studentService,
                             PaymentService paymentService,
                             UserRepository userRepository,
                             org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.studentService = studentService;
        this.paymentService = paymentService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register/{workshopId}")
    public ResponseEntity<Map<String, Object>> registerForWorkshop(
            @PathVariable Long workshopId,
            @Valid @RequestBody(required = false) TeamRegistrationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long studentId = getStudentId(userDetails);
        Map<String, Object> response = studentService.registerForWorkshop(workshopId, studentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my-workshops")
    public ResponseEntity<List<RegistrationResponse>> getMyWorkshops(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long studentId = getStudentId(userDetails);
        return ResponseEntity.ok(studentService.getMyWorkshops(studentId));
    }

    @PostMapping("/payment/{registrationId}")
    public ResponseEntity<Map<String, Object>> processPayment(
            @PathVariable Long registrationId,
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long studentId = getStudentId(userDetails);
        Map<String, Object> response = paymentService.processPayment(registrationId, request, studentId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/cancel/{registrationId}")
    public ResponseEntity<Map<String, Object>> cancelRegistration(
            @PathVariable Long registrationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long studentId = getStudentId(userDetails);
        Map<String, Object> response = studentService.cancelRegistration(registrationId, studentId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refund/{registrationId}")
    public ResponseEntity<Map<String, Object>> refundRegistration(
            @PathVariable Long registrationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long studentId = getStudentId(userDetails);
        Map<String, Object> response = studentService.refundRegistration(registrationId, studentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long studentId = getStudentId(userDetails);
        return ResponseEntity.ok(studentService.getProfile(studentId));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long studentId = getStudentId(userDetails);
        return ResponseEntity.ok(studentService.updateProfile(studentId, request));
    }

    @PutMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long studentId = getStudentId(userDetails);
        studentService.changePassword(studentId, request.getCurrentPassword(), request.getNewPassword(), passwordEncoder);
        
        java.util.Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "Password changed successfully.");
        return ResponseEntity.ok(response);
    }

    private Long getStudentId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found."))
                .getId();
    }
}
