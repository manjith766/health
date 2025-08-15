package com.noteric.health.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    public void sendOtpEmail(String to, String otp) {
        log.info("Sending OTP {} to email {}", otp, to);
    }
}
