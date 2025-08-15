package com.noteric.health.service;

import com.noteric.health.model.DietPlan;
import com.noteric.health.model.Report;
import com.noteric.health.model.TestResult;
import com.noteric.health.repository.DietPlanRepository;
import com.noteric.health.repository.TestResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DietPlanService {
    private final DietPlanRepository dietPlanRepo;
    private final TestResultRepository testResultRepo;
    private final PromptBuilderService promptBuilder;
    private final AIClientService aiClient;

    public DietPlan generateForReport(Report report) {
        List<TestResult> results = testResultRepo.findByReport(report);
        String prompt = promptBuilder.buildPrompt(report.getAge(), results);
        String planText = aiClient.generateDietPlan(prompt);

        DietPlan dietPlan = DietPlan.builder()
                .report(report)
                .generatedPlan(planText)
                .build();
        return dietPlanRepo.save(dietPlan);
    }
}
