package com.noteric.health.service;

import com.noteric.health.model.DietPlan;
import com.noteric.health.model.HealthTrend;
import com.noteric.health.model.Report;
import com.noteric.health.model.TestResult;
import com.noteric.health.model.User;
import com.noteric.health.repository.HealthTrendRepository;
import com.noteric.health.repository.ReportRepository;
import com.noteric.health.repository.TestResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    private final ReportRepository reportRepository;
    private final TestResultRepository testResultRepository;
    private final HealthTrendRepository trendRepository;
    private final OCRService ocrService;
    private final MetricParserService parserService;
    private final DietPlanService dietPlanService;  // Inject diet plan service

    public Report uploadReport(User user, Integer age, String reportType, MultipartFile file) throws IOException {
        // Extract text using OCR
        String ocrText = ocrService.extractTextFromFile(file);

        log.info("OCR Text Extracted: {}", ocrText);

        // Build and save the Report entity first
        Report report = Report.builder()
                .user(user)
                .age(age)
                .reportType(reportType)
                .filePath(file.getOriginalFilename())
                .ocrText(ocrText)
                .build();
        report = reportRepository.save(report);

        // Parse test results from OCR text
        List<TestResult> parsedResults = parserService.parseMetrics(ocrText);

        if (parsedResults.isEmpty()) {
            log.warn("No test metrics found in the uploaded report OCR text: {}", ocrText);
            throw new RuntimeException("No test metrics found in report. Please upload a clearer report.");
        }

        // Persist each TestResult and create HealthTrend for each
        for (TestResult tr : parsedResults) {
            tr.setReport(report);
            testResultRepository.save(tr);

            HealthTrend trend = HealthTrend.builder()
                    .user(user)
                    .metricName(tr.getMetricName())
                    .value(tr.getValue())
                    .recordedAt(LocalDate.now())
                    .build();
            trendRepository.save(trend);
        }

        // Automatically generate diet plan for the report
        DietPlan dietPlan = dietPlanService.generateForReport(report);
        log.info("Diet plan generated for report {} with id {}", report.getId(), dietPlan.getId());

        return report;
    }
}
