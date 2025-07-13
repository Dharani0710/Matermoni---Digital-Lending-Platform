package com.mate.demo.dto;

public record AdminRegisterRequest(
    String email,
    String password,
    String firstName,
    String lastName,
    String adminCode,
    String department,
    boolean isSuperAdmin
) {}