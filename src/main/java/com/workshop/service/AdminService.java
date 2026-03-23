// FILE: src/main/java/com/workshop/service/AdminService.java
package com.workshop.service;

import com.workshop.dto.*;
import com.workshop.exception.ResourceNotFoundException;
import com.workshop.exception.UnauthorizedAccessException;
import com.workshop.exception.UserNotFoundException;
import com.workshop.model.Payment;
import com.workshop.model.Registration;
import com.workshop.model.User;
import com.workshop.repository.PaymentRepository;
import com.workshop.repository.RegistrationRepository;
import com.workshop.repository.UserRepository;
import com.workshop.repository.WorkshopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final WorkshopRepository workshopRepository;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;
    private final PaymentRepository paymentRepository;

    public AdminService(WorkshopRepository workshopRepository,
                        UserRepository userRepository,
                        RegistrationRepository registrationRepository,
                        PaymentRepository paymentRepository) {
        this.workshopRepository = workshopRepository;
        this.userRepository = userRepository;
        this.registrationRepository = registrationRepository;
        this.paymentRepository = paymentRepository;
    }

    public AdminStatsResponse getStats() {
        long totalWorkshops = workshopRepository.count();
        long totalStudents = userRepository.countByRole("STUDENT");
        long totalRegistrations = registrationRepository.count();
        BigDecimal totalRevenue = paymentRepository.sumAmountByPaymentStatus("SUCCESS");
        long pendingPayments = paymentRepository.countByPaymentStatus("PENDING");
        long confirmedRegistrations = registrationRepository.countByStatus("CONFIRMED");

        return AdminStatsResponse.builder()
                .totalWorkshops(totalWorkshops)
                .totalStudents(totalStudents)
                .totalRegistrations(totalRegistrations)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .pendingPayments(pendingPayments)
                .confirmedRegistrations(confirmedRegistrations)
                .build();
    }

    @Transactional(readOnly = true)
    public List<AdminRegistrationResponse> getAllRegistrations() {
        return registrationRepository.findAll().stream()
                .map(this::toAdminRegResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AdminRegistrationResponse> getRegistrationsByWorkshop(Long workshopId) {
        return registrationRepository.findByWorkshopIdOrderByRegisteredAtDesc(workshopId)
                .stream()
                .map(this::toAdminRegResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> confirmRegistration(Long registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Registration not found with ID: " + registrationId));
        registration.setStatus("CONFIRMED");
        registrationRepository.save(registration);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "Registration confirmed successfully.");
        return response;
    }

    @Transactional
    public Map<String, Object> markAttended(Long registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Registration not found with ID: " + registrationId));
        registration.setAttended(true);
        registration.setCheckInTime(java.time.LocalDateTime.now());
        registrationRepository.save(registration);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "Checked in and marked as Attended.");
        return response;
    }

    @Transactional
    public Map<String, Object> completeSession(Long registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Registration not found with ID: " + registrationId));
        
        if (registration.getCheckInTime() == null) {
            throw new UnauthorizedAccessException("Cannot complete a session that hasn't started (Check-in first).");
        }
        
        registration.setCheckOutTime(java.time.LocalDateTime.now());
        registrationRepository.save(registration);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "Session completed and checked out.");
        return response;
    }

    @Transactional(readOnly = true)
    public AdminRegistrationResponse getRegistrationById(Long registrationId) {
        return registrationRepository.findById(registrationId)
                .map(this::toAdminRegResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Registration not found with ID: " + registrationId));
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toPaymentResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        return toUserResponse(user);
    }

    @Transactional
    public Map<String, Object> deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        if ("ADMIN".equals(user.getRole())) {
            throw new UnauthorizedAccessException("Admin account cannot be deleted.");
        }

        userRepository.delete(user);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "Student account deleted successfully.");
        return response;
    }

    // ── Private Mappers ────────────────────────────────────────────────────

    private AdminRegistrationResponse toAdminRegResponse(Registration reg) {
        Optional<Payment> paymentOpt = paymentRepository.findFirstByRegistrationIdOrderByCreatedAtDesc(reg.getId());
        String paymentStatus = paymentOpt.map(Payment::getPaymentStatus).orElse("NOT_PAID");
        String transactionId = paymentOpt.map(Payment::getTransactionId).orElse(null);

        return AdminRegistrationResponse.builder()
                .registrationId(reg.getId())
                .studentId(reg.getStudent() != null ? reg.getStudent().getId() : null)
                .studentName(reg.getStudent() != null ? reg.getStudent().getFullName() : "Unknown")
                .studentEmail(reg.getStudent() != null ? reg.getStudent().getEmail() : null)
                .workshopId(reg.getWorkshop() != null ? reg.getWorkshop().getId() : null)
                .workshopTitle(reg.getWorkshop() != null ? reg.getWorkshop().getTitle() : "N/A")
                .registeredAt(reg.getRegisteredAt())
                .registrationStatus(reg.getStatus())
                .paymentStatus(paymentStatus)
                .transactionId(transactionId)
                .teamMembersCount(reg.getTeamMembersCount())
                .teamMembers(reg.getTeamMembers())
                .attended(reg.getAttended())
                .checkInTime(reg.getCheckInTime())
                .checkOutTime(reg.getCheckOutTime())
                .studentPhone(reg.getStudent() != null ? reg.getStudent().getPhone() : null)
                .studentDepartment(reg.getStudent() != null ? reg.getStudent().getDepartment() : null)
                .studentRollNumber(reg.getStudent() != null ? reg.getStudent().getRollNumber() : null)
                .coordinatorName(reg.getWorkshop() != null ? reg.getWorkshop().getCoordinatorName() : null)
                .coordinatorPhone(reg.getWorkshop() != null ? reg.getWorkshop().getCoordinatorPhone() : null)
                .build();
    }

    private PaymentResponse toPaymentResponse(Payment p) {
        return PaymentResponse.builder()
                .paymentId(p.getId())
                .registrationId(p.getRegistration() != null ? p.getRegistration().getId() : null)
                .studentId(p.getStudent() != null ? p.getStudent().getId() : null)
                .studentName(p.getStudent() != null ? p.getStudent().getFullName() : "Unknown")
                .workshopId(p.getWorkshop() != null ? p.getWorkshop().getId() : null)
                .workshopTitle(p.getWorkshop() != null ? p.getWorkshop().getTitle() : "N/A")
                .amount(p.getAmount())
                .paymentMethod(p.getPaymentMethod())
                .paymentStatus(p.getPaymentStatus())
                .transactionId(p.getTransactionId())
                .cardLast4(p.getCardLast4())
                .paidAt(p.getPaidAt())
                .createdAt(p.getCreatedAt())
                .studentEmail(p.getStudent() != null ? p.getStudent().getEmail() : null)
                .studentPhone(p.getStudent() != null ? p.getStudent().getPhone() : null)
                .studentDepartment(p.getStudent() != null ? p.getStudent().getDepartment() : null)
                .studentRollNumber(p.getStudent() != null ? p.getStudent().getRollNumber() : null)
                .build();
    }

    private UserResponse toUserResponse(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .role(u.getRole())
                .phone(u.getPhone())
                .department(u.getDepartment())
                .rollNumber(u.getRollNumber())
                .createdAt(u.getCreatedAt())
                .build();
    }
}
