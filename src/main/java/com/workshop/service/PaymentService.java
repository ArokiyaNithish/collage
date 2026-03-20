// FILE: src/main/java/com/workshop/service/PaymentService.java
package com.workshop.service;

import com.workshop.dto.PaymentRequest;
import com.workshop.exception.*;
import com.workshop.model.Payment;
import com.workshop.model.Registration;
import com.workshop.repository.PaymentRepository;
import com.workshop.repository.RegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class PaymentService {

    private static final Pattern UPI_PATTERN = Pattern.compile("^\\w+@\\w+$");

    private final RegistrationRepository registrationRepository;
    private final PaymentRepository paymentRepository;

    public PaymentService(RegistrationRepository registrationRepository,
                          PaymentRepository paymentRepository) {
        this.registrationRepository = registrationRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Map<String, Object> processPayment(Long registrationId, PaymentRequest req, Long studentId) {
        // 1. Find registration
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Registration not found with ID: " + registrationId));

        // 2. Check ownership
        if (!registration.getStudent().getId().equals(studentId)) {
            throw new UnauthorizedAccessException("You can only pay for your own registrations.");
        }

        // 3. Check registration is not CANCELLED
        if ("CANCELLED".equals(registration.getStatus())) {
            throw new RegistrationCancelledException(
                    "This registration has been cancelled. Cannot process payment.");
        }

        // 4. Check payment not already done
        Optional<Payment> existingPayment = paymentRepository.findFirstByRegistrationIdOrderByCreatedAtDesc(registrationId);
        if (existingPayment.isPresent() && "SUCCESS".equals(existingPayment.get().getPaymentStatus())) {
            throw new PaymentAlreadyDoneException(
                    "Payment has already been completed for this registration. Transaction ID: "
                            + existingPayment.get().getTransactionId());
        }

        // 5. Validate payment details by method
        String method = req.getPaymentMethod().toUpperCase();
        String cardLast4 = null;

        switch (method) {
            case "CARD" -> {
                if (req.getCardNumber() == null || req.getCardNumber().isBlank()) {
                    throw new InvalidPaymentException("Card number is required.");
                }
                String digits = req.getCardNumber().replaceAll("\\s+", "");
                if (!digits.matches("\\d{16}")) {
                    throw new InvalidPaymentException("Card number must be exactly 16 digits.");
                }
                cardLast4 = digits.substring(12);
            }
            case "UPI" -> {
                if (req.getUpiId() == null || req.getUpiId().isBlank()) {
                    throw new InvalidPaymentException("UPI ID is required.");
                }
                if (!UPI_PATTERN.matcher(req.getUpiId().trim()).matches()) {
                    throw new InvalidPaymentException("Invalid UPI ID format. Example: name@upi");
                }
            }
            case "NET_BANKING" -> {
                if (req.getBankName() == null || req.getBankName().isBlank()) {
                    throw new InvalidPaymentException("Please select a bank for net banking.");
                }
            }
            case "CASH" -> {
                // No validation needed; student pays in person
            }
            default -> throw new InvalidPaymentException(
                    "Invalid payment method: " + req.getPaymentMethod()
                            + ". Supported: CARD, UPI, NET_BANKING, CASH");
        }

        // 6. Generate transaction ID
        String transactionId = "TXN" + UUID.randomUUID().toString()
                .replace("-", "").substring(0, 12).toUpperCase();

        // 7. Create payment record
        Payment payment = Payment.builder()
                .registration(registration)
                .student(registration.getStudent())
                .workshop(registration.getWorkshop())
                .amount(registration.getWorkshop().getFee())
                .paymentMethod(method)
                .paymentStatus("SUCCESS")
                .transactionId(transactionId)
                .cardLast4(cardLast4)
                .paidAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        // 8. Update registration status to CONFIRMED
        registration.setStatus("CONFIRMED");
        registrationRepository.save(registration);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("transactionId", transactionId);
        response.put("message", "Payment successful! Transaction ID: " + transactionId);
        return response;
    }
}
