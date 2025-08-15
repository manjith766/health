package com.noteric.health.service;

import com.noteric.health.model.TestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class MetricParserService {
    public List<TestResult> parseMetrics(String ocrText) {
        List<TestResult> results = new ArrayList<>();
        parseAndAdd(results, ocrText,
                "(?i)hemoglobin\\s*[:\\-]?\\s*(\\d+\\.?\\d*)\\s*(g/dl|g/dL)",
                "Hemoglobin", "13-17 g/dL");
        parseAndAdd(results, ocrText,
                "(?i)glucose\\s*[:\\-]?\\s*(\\d+\\.?\\d*)\\s*(mg/dl|mg/dL)",
                "Glucose", "70-100 mg/dL");
        parseAndAdd(results, ocrText,
                "(?i)cholesterol\\s*[:\\-]?\\s*(\\d+\\.?\\d*)\\s*(mg/dl|mg/dL)",
                "Cholesterol", "<200 mg/dL");
        parseAndAdd(results, ocrText,
                "(?i)triglycerides\\s*[:\\-]?\\s*(\\d+\\.?\\d*)\\s*(mg/dl|mg/dL)",
                "Triglycerides", "<150 mg/dL");
        return results;
    }

    private void parseAndAdd(List<TestResult> list, String text, String regex,
                             String metricName, String normalRange) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        if (matcher.find()) {
            BigDecimal value = new BigDecimal(matcher.group(1));
            String unit = matcher.group(2);
            log.info("Found {}: {} {}", metricName, value, unit);
            list.add(TestResult.builder()
                    .metricName(metricName)
                    .value(value)
                    .unit(unit)
                    .normalRange(normalRange)
                    .build());
        }
    }
}
