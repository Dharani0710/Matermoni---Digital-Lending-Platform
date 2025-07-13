package com.mate.demo.service;

import com.mate.demo.dto.*;
import com.mate.demo.entity.Admin;
import com.mate.demo.entity.User;
import com.mate.demo.exception.*;
import com.mate.demo.repository.AdminRepository;
import com.mate.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Admin management methods
    @Transactional
    public Admin createAdmin(AdminRegisterRequest request) {
        validateAdminRequest(request);

        if (adminRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email %s already in use".formatted(request.email()));
        }

        if (adminRepository.existsByAdminCode(request.adminCode())) {
            throw new DuplicateResourceException("Admin code %s already in use".formatted(request.adminCode()));
        }

        Admin admin = Admin.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .adminCode(request.adminCode())
                .department(request.department())
                .isSuperAdmin(request.isSuperAdmin())
                .build();

        return adminRepository.save(admin);
    }

    // User management methods
    public Page<User> getAllUsers(Pageable pageable, String search) {
        if (search != null && !search.isBlank()) {
            return userRepository.findByEmailContainingOrFirstNameContainingOrLastNameContaining(
                    search, search, search, pageable);
        }
        return userRepository.findAll(pageable);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Transactional
    public User updateUser(Long id, UserUpdateRequest request) {
        User user = getUserById(id);

        if (!user.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email %s already in use".formatted(request.getEmail()));
        }

        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public User toggleUserStatus(Long id) {
        User user = getUserById(id);
        user.setEnabled(!user.isEnabled());
        return userRepository.save(user);
    }

    // Validation methods
    private void validateAdminRequest(AdminRegisterRequest request) {
        if (request.email() == null || request.email().isBlank()) {
            throw new InvalidDataException("Email cannot be empty");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new InvalidDataException("Password cannot be empty");
        }
        if (request.firstName() == null || request.firstName().isBlank()) {
            throw new InvalidDataException("First name cannot be empty");
        }
        if (request.lastName() == null || request.lastName().isBlank()) {
            throw new InvalidDataException("Last name cannot be empty");
        }
        if (request.adminCode() == null || request.adminCode().isBlank()) {
            throw new InvalidDataException("Admin code cannot be empty");
        }
    }
}