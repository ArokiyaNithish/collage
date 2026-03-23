package com.workshop.controller;

import com.workshop.model.User;
import com.workshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin-view")
public class AdminViewController {

    @Autowired // Demonstrates Spring Dependency Injection
    private UserRepository userRepository;

    @GetMapping("/dashboard") // Demonstrates Spring MVC Annotation-based controller
    public String getDashboard(Model model) {
        // Fetch all users to display in the Thymeleaf template
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        
        // Return the name of the Thymeleaf template (admin-dashboard.html)
        return "admin-dashboard";
    }
}
