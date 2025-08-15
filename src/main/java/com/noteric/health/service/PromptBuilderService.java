package com.noteric.health.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PromptBuilderService {
    public String buildPrompt(Integer age, java.util.List<?> results) {
        // Simplified fixed prompt for testing AI response
        String prompt = "Create a 7-day vegetarian Indian diet plan with meals, timings, and portion sizes in Markdown format.";
        log.info("Generated AI prompt (test): {}", prompt);
        return prompt;
    }
}
