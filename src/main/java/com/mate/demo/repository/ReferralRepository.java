package com.mate.demo.repository;

import com.mate.demo.entity.Referral;
import com.mate.demo.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReferralRepository extends JpaRepository<Referral, Long> {
    Optional<Referral> findByReferralCode(String referralCode); // This must return Optional<Referral>
    
    boolean existsByReferralEmailAndReferringUser(String referralEmail, User referringUser);
    
    List<Referral> findByReferringUserId(Long userId);
    
    long countByReferringUserIdAndStatus(Long userId, Referral.ReferralStatus status);
}