package com.noteric.health.dto;

import java.util.List;

public record DashboardResponse(List<HealthTrendDto> trends,
                                List<TestResultDto> testHistory,
                                String latestDietPlan) {}