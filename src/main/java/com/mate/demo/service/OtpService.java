package com.mate.demo.service;

import com.mate.demo.entity.OtpVerification;
import com.mate.demo.repository.OtpVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRE_MINUTES = 5;
    
    private final OtpVerificationRepository otpRepository;

    @Transactional
    public String generateOtp(String email) {
        String otpCode = generateRandomOtp();
        
        OtpVerification otp = OtpVerification.builder()
                .email(email)
                .otpCode(otpCode)
                .expiryTime(LocalDateTime.now().plusMinutes(OTP_EXPIRE_MINUTES))
                .verified(false)
                .build();
        
        otpRepository.save(otp);
        return otpCode;
    }

    @Transactional
    public boolean verifyOtp(String email, String otpCode) {
        OtpVerification otp = otpRepository.findByEmailAndOtpCode(email, otpCode)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));
        
        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }
        
        otp.setVerified(true);
        otp.setVerifiedAt(LocalDateTime.now());
        otpRepository.save(otp);
        return true;
    }

    @Transactional
    public void cleanExpiredOtps() {
        otpRepository.deleteByExpiryTimeBefore(LocalDateTime.now());
    }

    private String generateRandomOtp() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}