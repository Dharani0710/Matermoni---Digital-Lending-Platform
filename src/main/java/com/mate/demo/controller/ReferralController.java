package com.mate.demo.controller;

import com.mate.demo.dto.ApiResponse;
import com.mate.demo.entity.Referral;
import com.mate.demo.exception.ResourceNotFoundException;
import com.mate.demo.service.ReferralService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/referrals")
@RequiredArgsConstructor
public class ReferralController {
    private final ReferralService referralService;

    @PostMapping
    public ResponseEntity<?> createReferral(
            @RequestParam Long userId,
            @RequestParam String referralEmail) {
        try {
            Referral referral = referralService.createReferral(userId, referralEmail);
            return ResponseEntity.ok(referral);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/complete")
    public ResponseEntity<?> completeReferral(@RequestParam String referralCode) {
        try {
            Referral referral = referralService.completeReferral(referralCode);
            return ResponseEntity.ok(referral);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserReferrals(@PathVariable Long userId) {
        try {
            List<Referral> referrals = referralService.getUserReferrals(userId);
            return ResponseEntity.ok(referrals);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(false, "Error fetching referrals"));
        }
    }

    @GetMapping("/count/{userId}")
    public ResponseEntity<?> countSuccessfulReferrals(@PathVariable Long userId) {
        try {
            long count = referralService.countSuccessfulReferrals(userId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(false, "Error counting referrals"));
        }
    }
}