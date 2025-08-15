package com.noteric.health.controller;

import com.noteric.health.dto.DashboardResponse;
import com.noteric.health.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(@RequestParam String email) {
        return ResponseEntity.ok(dashboardService.getDashboard(email));
    }
}
