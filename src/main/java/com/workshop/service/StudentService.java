// FILE: src/main/java/com/workshop/service/StudentService.java
package com.workshop.service;

import com.workshop.dto.ProfileUpdateRequest;
import com.workshop.dto.RegistrationResponse;
import com.workshop.dto.TeamRegistrationRequest;
import com.workshop.dto.UserProfileResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.exception.*;
import com.workshop.model.Payment;
import com.workshop.model.Registration;
import com.workshop.model.User;
import com.workshop.model.Workshop;
import com.workshop.repository.PaymentRepository;
import com.workshop.repository.RegistrationRepository;
import com.workshop.repository.UserRepository;
import com.workshop.repository.WorkshopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final WorkshopRepository workshopRepository;
    private final RegistrationRepository registrationRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public StudentService(WorkshopRepository workshopRepository,
                          RegistrationRepository registrationRepository,
                          PaymentRepository paymentRepository,
                          UserRepository userRepository) {
        this.workshopRepository = workshopRepository;
        this.registrationRepository = registrationRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Map<String, Object> registerForWorkshop(Long workshopId, Long studentId, TeamRegistrationRequest request) {
        // 1. Find workshop
        Workshop workshop = workshopRepository.findById(workshopId)
                .orElseThrow(() -> new WorkshopNotFoundException("Workshop not found with ID: " + workshopId));

        // 2. Check duplicate registration
        if (registrationRepository.existsByStudentIdAndWorkshopId(studentId, workshopId)) {
            throw new AlreadyRegisteredException(
                    "You are already registered for: " + workshop.getTitle());
        }

        int teamMembersCount = 1;
        String teamMembersJson = null;

        if (request != null && request.getTeamMembersCount() != null && request.getTeamMembersCount() > 1) {
            teamMembersCount = request.getTeamMembersCount();
            if (teamMembersCount > workshop.getTeamSize()) {
                 throw new IllegalArgumentException("Team size exceeds allowed workshop maximum of " + workshop.getTeamSize());
            }
            if (request.getTeamMembersList() != null && !request.getTeamMembersList().isEmpty()) {
                // Validate teammates are registered students
                for (TeamRegistrationRequest.TeamMember member : request.getTeamMembersList()) {
                    boolean isValid = userRepository.existsByRollNumberAndRole(member.getRollNumber(), "STUDENT");
                    // Assuming "STUDENT" is the role string used in your system
                    if (!isValid) {
                        throw new IllegalArgumentException("Teammate with Roll Number '" + member.getRollNumber() + "' is not a registered student. Only registered students can be in a team.");
                    }
                }
                try {
                    teamMembersJson = new ObjectMapper().writeValueAsString(request.getTeamMembersList());
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Failed to serialize team members", e);
                }
            }
        }

        // 3. Check seat availability
        if (workshop.getSeatsAvailable() < teamMembersCount) {
            throw new WorkshopFullException(
                    "Workshop '" + workshop.getTitle() + "' does not have enough seats available for a team of " + teamMembersCount + ".");
        }

        // 4. Decrement seat count
        workshop.setSeatsAvailable(workshop.getSeatsAvailable() - teamMembersCount);
        workshopRepository.save(workshop);

        // 5. Create registration
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new UserNotFoundException("Student not found with ID: " + studentId));

        Registration registration = Registration.builder()
                .student(student)
                .workshop(workshop)
                .teamMembersCount(teamMembersCount)
                .teamMembers(teamMembersJson)
                .status("PENDING")
                .build();

        Registration saved = registrationRepository.save(registration);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "Successfully registered for '" + workshop.getTitle() + "'. Please complete payment.");
        response.put("registrationId", saved.getId());
        return response;
    }

    @Transactional
    public Map<String, Object> cancelRegistration(Long registrationId, Long studentId) {
        // 1. Find registration
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Registration not found with ID: " + registrationId));

        // 2. Check ownership
        if (!registration.getStudent().getId().equals(studentId)) {
            throw new UnauthorizedAccessException("You can only cancel your own registrations.");
        }

        // 3. Check if payment already done
        Optional<Payment> payment = paymentRepository.findFirstByRegistrationIdOrderByCreatedAtDesc(registrationId);
        if (payment.isPresent() && "SUCCESS".equals(payment.get().getPaymentStatus())) {
            throw new PaymentAlreadyDoneException(
                    "Cannot cancel after payment. Please contact the admin for a refund.");
        }

        // 4. Mark as CANCELLED
        registration.setStatus("CANCELLED");
        registrationRepository.save(registration);

        // 5. Free up the seat
        Workshop workshop = registration.getWorkshop();
        workshop.setSeatsAvailable(workshop.getSeatsAvailable() + registration.getTeamMembersCount());
        workshopRepository.save(workshop);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "Registration cancelled successfully.");
        return response;
    }
    @Transactional
    public Map<String, Object> refundRegistration(Long registrationId, Long studentId) {
        // 1. Find registration
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Registration not found with ID: " + registrationId));

        // 2. Check ownership
        if (!registration.getStudent().getId().equals(studentId)) {
            throw new UnauthorizedAccessException("You can only refund your own registrations.");
        }

        // 3. Mark payment as REFUNDED if it was SUCCESS
        Optional<Payment> paymentOpt = paymentRepository.findFirstByRegistrationIdOrderByCreatedAtDesc(registrationId);
        boolean isRefunded = false;
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            if ("SUCCESS".equals(payment.getPaymentStatus())) {
                payment.setPaymentStatus("REFUNDED");
                paymentRepository.save(payment);
                isRefunded = true;
            }
        }

        if (!isRefunded) {
             throw new InvalidPaymentException("No successful payment found to refund.");
        }

        // 4. Mark registration as CANCELLED
        registration.setStatus("CANCELLED");
        registrationRepository.save(registration);

        // 5. Free up the seat
        Workshop workshop = registration.getWorkshop();
        workshop.setSeatsAvailable(workshop.getSeatsAvailable() + registration.getTeamMembersCount());
        workshopRepository.save(workshop);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "Refund processed successfully. Amount sent to your UPI ID.");
        return response;
    }
    @Transactional(readOnly = true)
    public List<RegistrationResponse> getMyWorkshops(Long studentId) {
        List<Registration> registrations =
                registrationRepository.findByStudentIdOrderByRegisteredAtDesc(studentId);

        return registrations.stream().map(reg -> {
            Optional<Payment> paymentOpt = paymentRepository.findFirstByRegistrationIdOrderByCreatedAtDesc(reg.getId());
            String paymentStatus = paymentOpt.map(Payment::getPaymentStatus).orElse(null);
            String transactionId = paymentOpt.map(Payment::getTransactionId).orElse(null);

            Workshop w = reg.getWorkshop();
            return RegistrationResponse.builder()
                    .registrationId(reg.getId())
                    .workshopId(w.getId())
                    .workshopTitle(w.getTitle())
                    .instructor(w.getInstructor())
                    .venue(w.getVenue())
                    .startDate(w.getStartDate())
                    .endDate(w.getEndDate())
                    .fee(w.getFee().multiply(new java.math.BigDecimal(reg.getTeamMembersCount().toString())))
                    .category(w.getCategory())
                    .registeredAt(reg.getRegisteredAt())
                    .teamMembersCount(reg.getTeamMembersCount())
                .teamMembers(reg.getTeamMembers())
                .attended(reg.getAttended())
                .checkInTime(reg.getCheckInTime())
                .checkOutTime(reg.getCheckOutTime())
                .registrationStatus(reg.getStatus())
                    .paymentStatus(paymentStatus)
                    .transactionId(transactionId)
                    .build();
        }).collect(Collectors.toList());
    }

    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        java.math.BigDecimal totalSpent = paymentRepository.sumAmountByStudentIdAndPaymentStatus(userId, "SUCCESS");
        if (totalSpent == null) totalSpent = java.math.BigDecimal.ZERO;

        return UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .phone(user.getPhone())
                .department(user.getDepartment())
                .rollNumber(user.getRollNumber())
                .address(user.getAddress())
                .hodContact(user.getHodContact())
                .totalAmountSpent(totalSpent)
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setDepartment(request.getDepartment());
        user.setRollNumber(request.getRollNumber());
        user.setAddress(request.getAddress());
        user.setHodContact(request.getHodContact());

        userRepository.save(user);
        return getProfile(userId);
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new UnauthorizedAccessException("Current password does not match");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
