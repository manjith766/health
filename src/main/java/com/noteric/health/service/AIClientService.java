package com.noteric.health.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIClientService {
    private final ObjectMapper objectMapper;

    @Value("${ollama.url:http://localhost:11434}")
    private String ollamaUrl;

    @Value("${ollama.model:llama3}")
    private String model;

    @Value("${ollama.timeout:180}")  // timeout in seconds
    private int timeoutSeconds;

    public String generateDietPlan(String prompt) {
        try {
            WebClient client = WebClient.builder().baseUrl(ollamaUrl).build();
            Map<String, Object> body = Map.of(
                    "model", model,
                    "prompt", prompt,
                    "stream", false
            );
            String responseBody = client.post()
                    .uri("/api/generate")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();

            Map<?, ?> responseMap = objectMapper.readValue(responseBody, Map.class);
            return (String) responseMap.get("response");
        } catch (Exception e) {
            log.error("AI request failed: {}", e.getMessage());
            throw new RuntimeException("Failed to generate diet plan", e);
        }
    }
}
