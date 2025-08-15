package com.noteric.health.controller;

import com.noteric.health.dto.DietPlanDto;
import com.noteric.health.model.DietPlan;
import com.noteric.health.model.Report;
import com.noteric.health.repository.ReportRepository;
import com.noteric.health.service.DietPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/diet")
@RequiredArgsConstructor
public class DietPlanController {

    private final ReportRepository reportRepo;
    private final DietPlanService dietPlanService;

    @PostMapping("/generate/{reportId}")
    public ResponseEntity<?> generate(@PathVariable Long reportId) {
        Optional<Report> reportOpt = reportRepo.findById(reportId);
        if (reportOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Report not found");
        }
        DietPlan plan = dietPlanService.generateForReport(reportOpt.get());
        // Build and return the DTO with ONLY safe fields
        DietPlanDto dto = new DietPlanDto(plan.getId(), plan.getGeneratedPlan());
        return ResponseEntity.ok(dto);
    }
}
