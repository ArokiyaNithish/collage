// FILE: src/main/java/com/workshop/repository/WorkshopRepository.java
package com.workshop.repository;

import com.workshop.model.Workshop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;

@Repository
public interface WorkshopRepository extends JpaRepository<Workshop, Long> {
    List<Workshop> findAllByOrderByCreatedAtDesc();

    @Query("SELECT w FROM Workshop w WHERE " +
           "(:title IS NULL OR LOWER(w.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:category IS NULL OR LOWER(w.category) LIKE LOWER(CONCAT('%', :category, '%'))) AND " +
           "(:startDate IS NULL OR w.startDate >= :startDate) " +
           "ORDER BY w.createdAt DESC")
    List<Workshop> searchWorkshops(@Param("title") String title, 
                                   @Param("category") String category, 
                                   @Param("startDate") LocalDateTime startDate);
}
