package com.noteric.health.service;

import com.noteric.health.model.User;
import com.noteric.health.repository.UserRepository;
import com.noteric.health.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    public String sendOtp(String email, String phone) {
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> User.builder()
                        .email(email)
                        .phone(phone)
                        .verified(false)
                        .build());
        String otp = String.format("%06d", new Random().nextInt(1_000_000));
        user.setOtpCode(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
        emailService.sendOtpEmail(email, otp);
        return "OTP sent to " + email;
    }

    public String verifyOtpAndGenerateToken(String email, String otp) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (otp.equals(user.getOtpCode()) && LocalDateTime.now().isBefore(user.getOtpExpiry())) {
                user.setVerified(true);
                user.setOtpCode(null);
                user.setOtpExpiry(null);
                userRepository.save(user);
                return jwtUtil.generateToken(email);
            }
        }
        throw new RuntimeException("Invalid or expired OTP.");
    }
}
