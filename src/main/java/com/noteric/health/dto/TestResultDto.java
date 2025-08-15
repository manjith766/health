package com.noteric.health.dto;

import java.math.BigDecimal;

public record TestResultDto(String metricName,
                            BigDecimal value,
                            String unit,
                            String normalRange) {}