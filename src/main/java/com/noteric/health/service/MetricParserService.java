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

        // Matches lines like: Hemoglobin (Hb) 125 Low 13.0-17.0 g/dl
        parseAndAdd(results, ocrText,
                "(?i)hemoglobin[^\\d]*(\\d+\\.?\\d*).{0,30}?g/?d[lL]", // Added flexibility for space, slash, upper/lower
                "Hemoglobin", "13.0-17.0 g/dL");

        // Matches lines like: Glucose (Random) 90 mg/dl or Glucose level 90 mg/dl
        parseAndAdd(results, ocrText,
                "(?i)glucose[^\\d]*(\\d+\\.?\\d*).{0,30}?mg/?d[lL]",
                "Glucose", "70-100 mg/dL");

        // Matches lines like: Cholesterol 180 mg/dl
        parseAndAdd(results, ocrText,
                "(?i)cholesterol[^\\d]*(\\d+\\.?\\d*).{0,30}?mg/?d[lL]",
                "Cholesterol", "<200 mg/dL");

        // Matches lines like: Triglycerides 140 mg/dl
        parseAndAdd(results, ocrText,
                "(?i)triglycerides[^\\d]*(\\d+\\.?\\d*).{0,30}?mg/?d[lL]",
                "Triglycerides", "<150 mg/dL");

        // Matches lines like: Platelet Count 150000 ... cumm
        parseAndAdd(results, ocrText,
                "(?i)platelet[^\\d]*(\\d+\\,?\\d*).{0,30}?cumm",
                "Platelet Count", "180000-410000 cumm");

        // Matches lines like: Total WBC count 9000 4000-11000 cumm
        parseAndAdd(results, ocrText,
                "(?i)wbc[^\\d]*(\\d+\\,?\\d*).{0,30}?cumm",
                "Total WBC count", "4000-11000 cumm");

        if (results.isEmpty()) {
            log.warn("No metrics matched in OCR text: {}", ocrText);
        }
        return results;
    }

    private void parseAndAdd(List<TestResult> list, String text, String regex,
                             String metricName, String normalRange) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        if (matcher.find()) {
            try {
                // Remove commas for numbers like "150,000"
                String valueStr = matcher.group(1).replaceAll(",", "");
                BigDecimal value = new BigDecimal(valueStr);
                String unit = extractUnitAfterValue(text, matcher.start(1) + matcher.group(1).length());
                log.info("Found metric {}: {} {}", metricName, value, unit);
                list.add(TestResult.builder()
                        .metricName(metricName)
                        .value(value)
                        .unit(unit)
                        .normalRange(normalRange)
                        .build());
            } catch (NumberFormatException e) {
                log.error("Failed to parse value for metric {}: {}", metricName, e.getMessage());
            }
        }
    }

    // Utility method to extract a short unit string after the value match
    private String extractUnitAfterValue(String text, int startIdx) {
        if (startIdx < text.length()) {
            // Look ahead for possible unit
            Matcher m = Pattern.compile("(g/dl|mg/dl|cumm|fl|%)", Pattern.CASE_INSENSITIVE).matcher(text.substring(startIdx));
            if (m.find()) {
                return m.group(1);
            }
        }
        return "";
    }
}
