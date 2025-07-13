package com.mate.demo.service;

import com.mate.demo.entity.Referral;
import com.mate.demo.entity.User;
import com.mate.demo.exception.ResourceNotFoundException;
import com.mate.demo.repository.ReferralRepository;
import com.mate.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReferralService {
    private final ReferralRepository referralRepository;
    private final UserRepository userRepository;

    @Transactional
    public Referral createReferral(Long userId, String referralEmail) {
        User referringUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (referralRepository.existsByReferralEmailAndReferringUser(referralEmail, referringUser)) {
            throw new IllegalStateException("Referral already exists for this email");
        }

        Referral referral = new Referral();
        referral.setReferringUser(referringUser);
        referral.setReferralEmail(referralEmail);
        referral.setReferralCode(generateReferralCode());
        referral.setStatus(Referral.ReferralStatus.PENDING);
        
        return referralRepository.save(referral);
    }

    @Transactional
    public Referral completeReferral(String referralCode) {
        // The key fix is here - ensure findByReferralCode returns Optional<Referral>
        Referral referral = referralRepository.findByReferralCode(referralCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Referral not found with code: " + referralCode
                ));
        
        referral.setStatus(Referral.ReferralStatus.COMPLETED);
        referral.setCompletedAt(LocalDateTime.now());
        return referralRepository.save(referral);
    }

    public List<Referral> getUserReferrals(Long userId) {
        return referralRepository.findByReferringUserId(userId);
    }

    public long countSuccessfulReferrals(Long userId) {
        return referralRepository.countByReferringUserIdAndStatus(
            userId, 
            Referral.ReferralStatus.COMPLETED
        );
    }

    private String generateReferralCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}