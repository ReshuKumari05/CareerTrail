package com.jobtracker.jobtrackerpro.repository;

import com.jobtracker.jobtrackerpro.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.jobtracker.jobtrackerpro.model.User;
import java.util.List;

import java.util.List;

public interface ApplicationRepository
        extends JpaRepository<Application, Long> {

    List<Application> findByCompanyNameContainingIgnoreCase(String companyName);
    List<Application> findByUser(User user);
}