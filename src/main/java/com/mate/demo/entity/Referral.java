package com.mate.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "referrals")
public class Referral {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "referring_user_id", nullable = false)
    private User referringUser;
    
    @Column(nullable = false)
    private String referralEmail;
    
    @Column(nullable = false, unique = true)
    private String referralCode;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReferralStatus status;
    
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public enum ReferralStatus {
        PENDING, COMPLETED
    }

    // No need for manual setters if using @Data from Lombok
    // Remove the orElseThrow method - it doesn't belong here

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}