package com.workshop;

import com.workshop.model.User;
import com.workshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Runs on every startup.
 * Ensures the admin user exists with a correctly BCrypt-encoded password.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // ── Fix admin password hash ───────────────────────────────────────
        userRepository.findByEmail("admin@college.edu").ifPresent(admin -> {
            String encoded = passwordEncoder.encode("Admin@123");
            admin.setPassword(encoded);
            userRepository.save(admin);
            log.info("✅ Admin password hash refreshed (Admin@123)");
        });

        // ── Create admin if it somehow doesn't exist ──────────────────────
        if (!userRepository.existsByEmail("admin@college.edu")) {
            User admin = User.builder()
                    .fullName("Admin User")
                    .email("admin@college.edu")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role("ADMIN")
                    .phone("9000000000")
                    .department("Administration")
                    .build();
            userRepository.save(admin);
            log.info("✅ Admin user created (admin@college.edu / Admin@123)");
        }
    }
}
