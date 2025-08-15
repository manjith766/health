package com.noteric.health.service;

import com.noteric.health.dto.DashboardResponse;
import com.noteric.health.dto.HealthTrendDto;
import com.noteric.health.dto.TestResultDto;
import com.noteric.health.model.DietPlan;
import com.noteric.health.model.Report;
import com.noteric.health.model.User;
import com.noteric.health.model.TestResult;
import com.noteric.health.repository.DietPlanRepository;
import com.noteric.health.repository.HealthTrendRepository;
import com.noteric.health.repository.ReportRepository;
import com.noteric.health.repository.TestResultRepository;
import com.noteric.health.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final HealthTrendRepository trendRepo;
    private final ReportRepository reportRepo;
    private final TestResultRepository testResultRepo;
    private final DietPlanRepository dietPlanRepo;
    private final UserRepository userRepo;

    public DashboardResponse getDashboard(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<HealthTrendDto> trends = trendRepo.findByUser(user).stream()
                .map(t -> new HealthTrendDto(t.getMetricName(), t.getValue(), t.getRecordedAt()))
                .collect(Collectors.toList());

        List<TestResultDto> history = reportRepo.findByUser(user).stream()
                .flatMap(r -> testResultRepo.findByReport(r).stream())
                .map(tr -> new TestResultDto(tr.getMetricName(), tr.getValue(), tr.getUnit(), tr.getNormalRange()))
                .collect(Collectors.toList());

        String latestDietPlan = reportRepo.findByUser(user).stream()
                .max(Comparator.comparing(Report::getCreatedAt))
                .flatMap(r -> dietPlanRepo.findByReport(r))
                .map(DietPlan::getGeneratedPlan)
                .orElse(null);

        return new DashboardResponse(trends, history, latestDietPlan);
    }
}
