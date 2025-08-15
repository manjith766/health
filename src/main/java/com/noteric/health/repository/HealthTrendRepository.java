package com.noteric.health.repository;

import com.noteric.health.model.HealthTrend;
import com.noteric.health.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HealthTrendRepository extends JpaRepository<HealthTrend, Long> {
    List<HealthTrend> findByUser(User user);
    List<HealthTrend> findByUserAndMetricName(User user, String metricName);
    List<HealthTrend> findByUserAndMetricNameAndRecordedAtBetween(
            User user, String metricName, LocalDate start, LocalDate end);
}
