// FILE: src/main/java/com/workshop/service/AuthService.java
package com.workshop.service;

import com.workshop.dto.*;
import com.workshop.exception.EmailAlreadyExistsException;
import com.workshop.exception.InvalidCredentialsException;
import com.workshop.exception.WeakPasswordException;
import com.workshop.model.User;
import com.workshop.repository.UserRepository;
import com.workshop.security.JwtUtils;
import com.workshop.security.UserDetailsServiceImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Z])(?=.*\\d).{8,}$");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils,
                       UserDetailsServiceImpl userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    public Map<String, Object> register(RegisterRequest req) {
        // 1. Check email uniqueness
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered: " + req.getEmail());
        }

        // 2. Validate password strength
        if (!PASSWORD_PATTERN.matcher(req.getPassword()).matches()) {
            throw new WeakPasswordException(
                    "Password must be at least 8 characters and contain at least 1 uppercase letter and 1 digit.");
        }

        // 3. Confirm passwords match
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new WeakPasswordException("Passwords do not match.");
        }

        // 4. Encode password and save user
        User user = User.builder()
                .fullName(req.getFullName().trim())
                .email(req.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(req.getPassword()))
                .role("STUDENT")
                .phone(req.getPhone())
                .department(req.getDepartment())
                .rollNumber(req.getRollNumber())
                .externalUser(req.getExternalUser() != null ? req.getExternalUser() : false)
                .build();

        User saved = userRepository.save(user);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "Account created successfully. You can now log in.");
        response.put("userId", saved.getId());
        return response;
    }

    public AuthResponse login(LoginRequest req) {
        // 1. Find user by email
        User user = userRepository.findByEmail(req.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password."));

        // 2. Verify password
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password.");
        }

        // 3. Generate JWT
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtils.generateToken(userDetails, user.getRole(), user.getId());

        return AuthResponse.builder()
                .token(token)
                .role(user.getRole())
                .fullName(user.getFullName())
                .userId(user.getId())
                .build();
    }

    public List<StudentSummaryResponse> getAllStudents() {
        return userRepository.findAllByRoleOrderByFullNameAsc("STUDENT")
                .stream()
                .map(u -> StudentSummaryResponse.builder()
                        .id(u.getId())
                        .fullName(u.getFullName())
                        .department(u.getDepartment())
                        .email(u.getEmail())
                        .rollNumber(u.getRollNumber())
                        .build())
                .collect(Collectors.toList());
    }
}
