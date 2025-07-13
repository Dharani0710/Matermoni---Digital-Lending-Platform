package com.mate.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReferralRequest {
    @NotNull(message = "Referring user ID is required")
    private Long referringUserId;

    @NotBlank(message = "Referral email is required")
    private String referralEmail;

    @Size(max = 500, message = "Note cannot exceed 500 characters")
    private String note;

    // Getters and Setters
    public Long getReferringUserId() {
        return referringUserId;
    }

    public void setReferringUserId(Long referringUserId) {
        this.referringUserId = referringUserId;
    }

    public String getReferralEmail() {
        return referralEmail;
    }

    public void setReferralEmail(String referralEmail) {
        this.referralEmail = referralEmail;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}