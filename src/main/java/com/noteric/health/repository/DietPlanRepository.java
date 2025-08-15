package com.noteric.health.repository;

import com.noteric.health.model.DietPlan;
import com.noteric.health.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DietPlanRepository extends JpaRepository<DietPlan, Long> {
    List<DietPlan> findByReport(Report report);

}
