package com.noteric.health.controller;

import com.noteric.health.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String email, @RequestParam String phone) {
        return ResponseEntity.ok(authService.sendOtp(email, phone));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        try {
            String jwtToken = authService.verifyOtpAndGenerateToken(email, otp);
            return ResponseEntity.ok(new JwtResponse(jwtToken));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public record JwtResponse(String token) {}
}
