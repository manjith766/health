package com.noteric.health.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record HealthTrendDto(String metricName,
                             BigDecimal value,
                             LocalDate recordedAt) {}
