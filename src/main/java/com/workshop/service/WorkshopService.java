// FILE: src/main/java/com/workshop/service/WorkshopService.java
package com.workshop.service;

import com.workshop.dto.WorkshopRequest;
import com.workshop.dto.WorkshopResponse;
import com.workshop.exception.WorkshopDateException;
import com.workshop.exception.WorkshopNotFoundException;
import com.workshop.model.Workshop;
import com.workshop.repository.WorkshopRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkshopService {

    private final WorkshopRepository workshopRepository;

    public WorkshopService(WorkshopRepository workshopRepository) {
        this.workshopRepository = workshopRepository;
    }

    public List<WorkshopResponse> getAllWorkshops() {
        return workshopRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<WorkshopResponse> searchWorkshops(String title, String category, LocalDateTime startDate) {
        return workshopRepository.searchWorkshops(title, category, startDate)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public WorkshopResponse getWorkshopById(Long id) {
        Workshop workshop = workshopRepository.findById(id)
                .orElseThrow(() -> new WorkshopNotFoundException("Workshop not found with ID: " + id));
        return toResponse(workshop);
    }

    public WorkshopResponse createWorkshop(WorkshopRequest req, Long adminId) {
        validateDates(req.getStartDate(), req.getEndDate());

        Workshop workshop = Workshop.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .instructor(req.getInstructor())
                .venue(req.getVenue())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .capacity(req.getCapacity())
                .teamSize(req.getTeamSize() != null ? req.getTeamSize() : 1)
                .seatsAvailable(req.getCapacity())
                .fee(req.getFee())
                .category(req.getCategory())
                .coordinatorName(req.getCoordinatorName())
                .coordinatorPhone(req.getCoordinatorPhone())
                .coordinatorEmail(req.getCoordinatorEmail())
                .status(req.getStatus() != null ? req.getStatus() : "UPCOMING")
                .createdBy(adminId)
                .build();

        return toResponse(workshopRepository.save(workshop));
    }

    public WorkshopResponse updateWorkshop(Long id, WorkshopRequest req) {
        Workshop existing = workshopRepository.findById(id)
                .orElseThrow(() -> new WorkshopNotFoundException("Workshop not found with ID: " + id));

        validateDates(req.getStartDate(), req.getEndDate());

        // Adjust seatsAvailable proportionally if capacity changed
        int oldCapacity = existing.getCapacity();
        int newCapacity = req.getCapacity();
        int registeredCount = oldCapacity - existing.getSeatsAvailable();
        int newSeatsAvailable = Math.max(0, newCapacity - registeredCount);

        existing.setTitle(req.getTitle());
        existing.setDescription(req.getDescription());
        existing.setInstructor(req.getInstructor());
        existing.setVenue(req.getVenue());
        existing.setStartDate(req.getStartDate());
        existing.setEndDate(req.getEndDate());
        existing.setCapacity(newCapacity);
        if (req.getTeamSize() != null) {
            existing.setTeamSize(req.getTeamSize());
        }
        existing.setSeatsAvailable(newSeatsAvailable);
        existing.setFee(req.getFee());
        existing.setCategory(req.getCategory());
        existing.setCoordinatorName(req.getCoordinatorName());
        existing.setCoordinatorPhone(req.getCoordinatorPhone());
        existing.setCoordinatorEmail(req.getCoordinatorEmail());
        if (req.getStatus() != null && !req.getStatus().isBlank()) {
            existing.setStatus(req.getStatus());
        }

        return toResponse(workshopRepository.save(existing));
    }

    public void deleteWorkshop(Long id) {
        Workshop workshop = workshopRepository.findById(id)
                .orElseThrow(() -> new WorkshopNotFoundException("Workshop not found with ID: " + id));
        workshopRepository.delete(workshop);
    }

    private void validateDates(LocalDateTime startDate, LocalDateTime endDate) {
        if (endDate.isBefore(startDate)) {
            throw new WorkshopDateException("End date must be after start date.");
        }
        if (startDate.isBefore(LocalDateTime.now())) {
            throw new WorkshopDateException("Start date cannot be in the past.");
        }
    }

    public WorkshopResponse toResponse(Workshop w) {
        return WorkshopResponse.builder()
                .id(w.getId())
                .title(w.getTitle())
                .description(w.getDescription())
                .instructor(w.getInstructor())
                .venue(w.getVenue())
                .startDate(w.getStartDate())
                .endDate(w.getEndDate())
                .capacity(w.getCapacity())
                .teamSize(w.getTeamSize())
                .seatsAvailable(w.getSeatsAvailable())
                .fee(w.getFee())
                .category(w.getCategory())
                .coordinatorName(w.getCoordinatorName())
                .coordinatorPhone(w.getCoordinatorPhone())
                .coordinatorEmail(w.getCoordinatorEmail())
                .status(w.getStatus())
                .createdAt(w.getCreatedAt())
                .updatedAt(w.getUpdatedAt())
                .build();
    }
}
