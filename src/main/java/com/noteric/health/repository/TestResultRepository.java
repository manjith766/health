package com.noteric.health.repository;

import com.noteric.health.model.Report;
import com.noteric.health.model.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    List<TestResult> findByReport(Report report);
}
