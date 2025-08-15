package com.noteric.health.repository;

import com.noteric.health.model.Report;
import com.noteric.health.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByUser(User user);
}
