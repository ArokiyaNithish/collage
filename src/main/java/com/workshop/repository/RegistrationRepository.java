// FILE: src/main/java/com/workshop/repository/RegistrationRepository.java
package com.workshop.repository;

import com.workshop.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByStudentIdOrderByRegisteredAtDesc(Long studentId);
    List<Registration> findByWorkshopIdOrderByRegisteredAtDesc(Long workshopId);
    boolean existsByStudentIdAndWorkshopId(Long studentId, Long workshopId);
    long countByStatus(String status);

    @Query("SELECT r.workshop.id FROM Registration r WHERE r.student.id = :studentId")
    List<Long> findWorkshopIdsByStudentId(Long studentId);

    Optional<Registration> findByIdAndStudentId(Long id, Long studentId);
}
