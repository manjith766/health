package com.noteric.health.controller;

import com.noteric.health.model.Report;
import com.noteric.health.model.User;
import com.noteric.health.repository.UserRepository;
import com.noteric.health.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final UserRepository userRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadReport(@RequestParam String email,
                                          @RequestParam Integer age,
                                          @RequestParam String reportType,
                                          @RequestParam("file") MultipartFile file) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        try {
            Report report = reportService.uploadReport(userOpt.get(), age, reportType, file);
            return ResponseEntity.ok(report);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to upload or process file: " + e.getMessage());
        }
    }
}
