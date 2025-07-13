package com.mate.demo.controller;

import com.mate.demo.dto.*;
import com.mate.demo.entity.Admin;
import com.mate.demo.entity.User;
import com.mate.demo.security.JwtService;
import com.mate.demo.service.AdminService;
import com.mate.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AdminService adminService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        User user = userService.registerUser(request);
        return ResponseEntity.ok(
            new ApiResponse(true, "User registered successfully", user.getId())
        );
    }

    @PostMapping("/admin/register")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse> registerAdmin(@Valid @RequestBody AdminRegisterRequest request) {
        Admin admin = adminService.createAdmin(request);
        return ResponseEntity.ok(
            new ApiResponse(true, "Admin registered successfully", admin.getId())
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        String jwt = jwtService.generateToken(authentication);
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}