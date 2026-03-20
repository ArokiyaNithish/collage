// FILE: src/main/java/com/workshop/repository/WorkshopRepository.java
package com.workshop.repository;

import com.workshop.model.Workshop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkshopRepository extends JpaRepository<Workshop, Long> {
    List<Workshop> findAllByOrderByCreatedAtDesc();
}
