package com.mate.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mate.demo.service.OtpService;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
public class OtpController {
    private final OtpService otpService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateOtp(@RequestParam String email) {
        String otp = otpService.generateOtp(email);
        return ResponseEntity.ok(otp);
    }

    @PostMapping("/verify")
    public ResponseEntity<Boolean> verifyOtp(
            @RequestParam String email,
            @RequestParam String otpCode) {
        boolean verified = otpService.verifyOtp(email, otpCode);
        return ResponseEntity.ok(verified);
    }

    @PostMapping("/clean")
    public ResponseEntity<Void> cleanExpiredOtps() {
        otpService.cleanExpiredOtps();
        return ResponseEntity.noContent().build();
    }
}