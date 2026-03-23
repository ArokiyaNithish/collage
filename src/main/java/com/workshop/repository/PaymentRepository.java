// FILE: src/main/java/com/workshop/repository/PaymentRepository.java
package com.workshop.repository;

import com.workshop.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByOrderByCreatedAtDesc();
    Optional<Payment> findFirstByRegistrationIdOrderByCreatedAtDesc(Long registrationId);
    long countByPaymentStatus(String paymentStatus);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.paymentStatus = :status")
    BigDecimal sumAmountByPaymentStatus(String status);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.student.id = :studentId AND p.paymentStatus = :status")
    BigDecimal sumAmountByStudentIdAndPaymentStatus(Long studentId, String status);
}
