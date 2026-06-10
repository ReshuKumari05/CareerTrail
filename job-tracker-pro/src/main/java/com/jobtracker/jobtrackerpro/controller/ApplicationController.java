package com.jobtracker.jobtrackerpro.controller;
import java.util.List;
import com.jobtracker.jobtrackerpro.model.Application;
import com.jobtracker.jobtrackerpro.model.ApplicationStatus;
import com.jobtracker.jobtrackerpro.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.jobtracker.jobtrackerpro.model.User;
import com.jobtracker.jobtrackerpro.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ApplicationController {

    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/save")
    public String saveApplication(

            @RequestParam(required = false) Long id,
            @RequestParam String companyName,
            @RequestParam String role,
            @RequestParam String location,
            @RequestParam String appliedDate,
            @RequestParam ApplicationStatus status,
            @RequestParam String notes,
            @RequestParam String source,
            @RequestParam(required = false) String interviewDate,
            Authentication authentication

    ) {

        Application application;

        if(id != null){

            application = applicationRepository
                    .findById(id)
                    .orElse(new Application());

        } else {

            application = new Application();
        }

        application.setCompanyName(companyName);
        application.setRole(role);
        application.setLocation(location);
        application.setAppliedDate(appliedDate);
        application.setStatus(status);
        application.setNotes(notes);
        application.setSource(source);
        application.setInterviewDate(interviewDate);

        String email = authentication.getName();

        User user = userRepository.findByEmail(email);

        application.setUser(user);

        applicationRepository.save(application);

        return "redirect:/applications";
    }

    @GetMapping("/applications")
    public String viewApplications(
            Model model,
            Authentication authentication
    ) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email);

        List<Application> applications =
                applicationRepository.findByUser(user);

        model.addAttribute(
                "applications",
                applications
        );

        long appliedCount = applications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.APPLIED)
                .count();

        long interviewCount = applications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.INTERVIEW_SCHEDULED)
                .count();

        long selectedCount = applications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.SELECTED)
                .count();

        long rejectedCount = applications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.REJECTED)
                .count();

        model.addAttribute("appliedCount", appliedCount);
        model.addAttribute("interviewCount", interviewCount);
        model.addAttribute("selectedCount", selectedCount);
        model.addAttribute("rejectedCount", rejectedCount);

        return "applications";
    }

    @GetMapping("/delete/{id}")
    public String deleteApplication(@PathVariable Long id) {

        applicationRepository.deleteById(id);

        return "redirect:/applications";
    }
    @GetMapping("/updateStatus/{id}")
    public String updateStatus(@PathVariable Long id) {

        Application application =
                applicationRepository.findById(id).orElse(null);

        if (application != null) {

            if (application.getStatus() == ApplicationStatus.APPLIED) {
                application.setStatus(ApplicationStatus.INTERVIEW_SCHEDULED);

            } else if (application.getStatus() == ApplicationStatus.INTERVIEW_SCHEDULED) {
                application.setStatus(ApplicationStatus.SELECTED);

            } else if (application.getStatus() == ApplicationStatus.SELECTED) {
                application.setStatus(ApplicationStatus.REJECTED);

            } else {
                application.setStatus(ApplicationStatus.APPLIED);
            }

            applicationRepository.save(application);
        }

        return "redirect:/applications";
    }
    @GetMapping("/edit/{id}")
    public String editApplication(
            @PathVariable Long id,
            Model model
    ) {

        Application application =
                applicationRepository.findById(id)
                        .orElse(null);
        System.out.println("EDIT CLICKED: " + application.getCompanyName());
        model.addAttribute(
                "application",
                application
        );

        return "add-application";
    }
    @GetMapping("/search")
    public String searchApplications(
            @RequestParam String keyword,
            Model model
    ) {

        model.addAttribute(
                "applications",
                applicationRepository
                        .findByCompanyNameContainingIgnoreCase(keyword)
        );

        return "applications";
    }
}