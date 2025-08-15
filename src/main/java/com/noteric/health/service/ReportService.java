package com.noteric.health.service;

import com.noteric.health.model.HealthTrend;
import com.noteric.health.model.Report;
import com.noteric.health.model.TestResult;
import com.noteric.health.model.User;
import com.noteric.health.repository.HealthTrendRepository;
import com.noteric.health.repository.ReportRepository;
import com.noteric.health.repository.TestResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final TestResultRepository testResultRepository;
    private final HealthTrendRepository trendRepository;
    private final OCRService ocrService;
    private final MetricParserService parserService;

    public Report uploadReport(User user, Integer age, String reportType, MultipartFile file) throws IOException {
        String ocrText = ocrService.extractTextFromFile(file);

        // Build and save the Report entity
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

        // Persist each TestResult and corresponding HealthTrend
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

        return report;
    }
}
