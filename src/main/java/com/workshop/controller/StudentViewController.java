package com.workshop.controller;

import com.workshop.model.Workshop;
import com.workshop.repository.WorkshopRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/student-view")
public class StudentViewController {

    private final WorkshopRepository workshopRepository;

    public StudentViewController(WorkshopRepository workshopRepository) {
        this.workshopRepository = workshopRepository;
    }

    @GetMapping("/dashboard")
    public String getStudentDashboard(Model model) {
        // Fetch upcoming workshops (start date in future) to satisfy syllabus requirement
        List<Workshop> upcomingWorkshops = workshopRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .filter(w -> w.getStartDate().isAfter(LocalDateTime.now()))
                .filter(w -> !"CANCELLED".equalsIgnoreCase(w.getStatus()))
                .toList();

        model.addAttribute("workshops", upcomingWorkshops);
        return "student-dashboard";
    }
}
