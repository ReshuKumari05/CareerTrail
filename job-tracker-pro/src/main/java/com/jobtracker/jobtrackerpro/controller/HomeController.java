package com.jobtracker.jobtrackerpro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import com.jobtracker.jobtrackerpro.repository.UserRepository;
import com.jobtracker.jobtrackerpro.repository.ApplicationRepository;
import com.jobtracker.jobtrackerpro.model.User;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import com.jobtracker.jobtrackerpro.model.Application;
import com.jobtracker.jobtrackerpro.model.ApplicationStatus;



@Controller
public class HomeController {
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    public HomeController(
            UserRepository userRepository,
            ApplicationRepository applicationRepository
    ) {
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
    }
    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/add")
    public String addApplication() {
        return "add-application";
    }
    @GetMapping("/profile")
    public String profile(
            Model model,
            Authentication authentication
    ) {


        String email = authentication.getName();

        User user = userRepository.findByEmail(email);

        model.addAttribute("user", user);

        return "profile";


    }
    @PostMapping("/save-profile")
    public String saveProfile(
            @RequestParam String name,
            @RequestParam String currentCompany,
            @RequestParam String currentRole,
            @RequestParam String experienceLevel,
            @RequestParam String currentLocation,
            Authentication authentication
    ) {


        String email = authentication.getName();

        User user = userRepository.findByEmail(email);

        user.setName(name);
        user.setCurrentCompany(currentCompany);
        user.setCurrentRole(currentRole);
        user.setExperienceLevel(experienceLevel);
        user.setCurrentLocation(currentLocation);

        userRepository.save(user);

        return "redirect:/profile?saved=true";


    }
    @GetMapping("/analytics")
    public String analytics(
            Model model,
            Authentication authentication
    ) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email);

        List<Application> applications =
                applicationRepository.findByUser(user);

        long total = applications.size();

        long applied = applications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.APPLIED)
                .count();

        long interview = applications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.INTERVIEW_SCHEDULED)
                .count();

        long selected = applications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.SELECTED)
                .count();

        long rejected = applications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.REJECTED)
                .count();


        double selectionRate = total == 0 ? 0 : (selected * 100.0) / total;
        double rejectionRate = total == 0 ? 0 : (rejected * 100.0) / total;
        double interviewRate = total == 0 ? 0 : (interview * 100.0) / total;
        double appliedRate = total == 0 ? 0 : (applied * 100.0) / total;

        model.addAttribute("selectionRate", selectionRate);
        model.addAttribute("rejectionRate", rejectionRate);
        model.addAttribute("interviewRate", interviewRate);
        model.addAttribute("appliedRate", appliedRate);

        model.addAttribute("total", total);
        model.addAttribute("applied", applied);
        model.addAttribute("interview", interview);
        model.addAttribute("selected", selected);
        model.addAttribute("rejected", rejected);
        return "analytics";
    }
}