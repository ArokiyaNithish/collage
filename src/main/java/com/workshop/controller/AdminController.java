// FILE: src/main/java/com/workshop/controller/AdminController.java
package com.workshop.controller;

import com.workshop.dto.*;
import com.workshop.service.AdminService;
import com.workshop.service.WorkshopService;
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
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final WorkshopService workshopService;
    private final UserRepository userRepository;

    public AdminController(AdminService adminService,
                           WorkshopService workshopService,
                           UserRepository userRepository) {
        this.adminService = adminService;
        this.workshopService = workshopService;
        this.userRepository = userRepository;
    }

    // ── Stats ─────────────────────────────────────────────────────────────

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    // ── Workshop Management ───────────────────────────────────────────────

    @PostMapping("/workshops")
    public ResponseEntity<WorkshopResponse> createWorkshop(
            @Valid @RequestBody WorkshopRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long adminId = getAdminId(userDetails);
        WorkshopResponse response = workshopService.createWorkshop(request, adminId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/workshops/{id}")
    public ResponseEntity<WorkshopResponse> updateWorkshop(
            @PathVariable Long id,
            @Valid @RequestBody WorkshopRequest request) {
        return ResponseEntity.ok(workshopService.updateWorkshop(id, request));
    }

    @DeleteMapping("/workshops/{id}")
    public ResponseEntity<Map<String, Object>> deleteWorkshop(@PathVariable Long id) {
        workshopService.deleteWorkshop(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Workshop deleted successfully."));
    }

    // ── Registration Management ───────────────────────────────────────────

    @GetMapping("/registrations")
    public ResponseEntity<List<AdminRegistrationResponse>> getAllRegistrations() {
        return ResponseEntity.ok(adminService.getAllRegistrations());
    }

    @GetMapping("/registrations/{workshopId}")
    public ResponseEntity<List<AdminRegistrationResponse>> getRegistrationsByWorkshop(
            @PathVariable Long workshopId) {
        return ResponseEntity.ok(adminService.getRegistrationsByWorkshop(workshopId));
    }

    @PutMapping("/registrations/{id}/confirm")
    public ResponseEntity<Map<String, Object>> confirmRegistration(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.confirmRegistration(id));
    }

    @GetMapping("/registrations/ticket/{id}")
    public ResponseEntity<AdminRegistrationResponse> getRegistrationById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getRegistrationById(id));
    }

    @PutMapping("/registrations/{id}/attend")
    public ResponseEntity<Map<String, Object>> markAttended(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.markAttended(id));
    }

    @PutMapping("/registrations/{id}/complete")
    public ResponseEntity<Map<String, Object>> completeSession(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.completeSession(id));
    }

    // ── Payment Management ───────────────────────────────────────────────

    @GetMapping("/payments")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(adminService.getAllPayments());
    }

    // ── User Management ───────────────────────────────────────────────────

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.deleteUser(id));
    }

    private Long getAdminId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Authenticated admin not found."))
                .getId();
    }
}
