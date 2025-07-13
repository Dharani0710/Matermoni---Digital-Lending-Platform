package com.mate.demo.service;

import com.mate.demo.dto.*;
import com.mate.demo.entity.User;
import com.mate.demo.exception.*;
import com.mate.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(UserRegisterRequest request) {
        validateUserRegisterRequest(request);
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already in use");
        }

        User user = User.builder()
                .email(validateEmail(request.getEmail()))
                .password(encodePassword(request.getPassword()))
                .firstName(validateName(request.getFirstName(), "First name"))
                .lastName(validateName(request.getLastName(), "Last name"))
                .phoneNumber(validatePhoneNumber(request.getPhoneNumber()))
                .enabled(true)
                .accountNonLocked(true)
                .role(User.Role.USER)
                .build();

        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Transactional
    public User updateProfile(Long userId, ProfileUpdateRequest request) {
        validateProfileUpdateRequest(request);
        User user = getUserById(userId);

        user.setFirstName(validateName(request.getFirstName(), "First name"));
        user.setLastName(validateName(request.getLastName(), "Last name"));
        user.setPhoneNumber(validatePhoneNumber(request.getPhoneNumber()));
        
        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        validateChangePasswordRequest(request);
        User user = getUserById(userId);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new InvalidPasswordException("New password must be different from current password");
        }

        user.setPassword(encodePassword(request.getNewPassword()));
        userRepository.save(user);
    }

    // ... (keep all your validation methods as they were)

    private void validateUserRegisterRequest(UserRegisterRequest request) {
        if (request == null) {
            throw new InvalidInputException("Registration request cannot be null");
        }
    }

    private void validateProfileUpdateRequest(ProfileUpdateRequest request) {
        if (request == null) {
            throw new InvalidInputException("Profile update request cannot be null");
        }
    }

    private void validateChangePasswordRequest(ChangePasswordRequest request) {
        if (request == null) {
            throw new InvalidInputException("Password change request cannot be null");
        }
        if (!StringUtils.hasText(request.getNewPassword())) {
            throw new InvalidInputException("New password cannot be empty");
        }
        if (request.getNewPassword().length() < 8) {
            throw new InvalidInputException("New password must be at least 8 characters");
        }
    }

    private String validateEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new InvalidInputException("Email cannot be empty");
        }
        if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new InvalidInputException("Invalid email format");
        }
        return email;
    }

    private String validateName(String name, String fieldName) {
        if (!StringUtils.hasText(name)) {
            throw new InvalidInputException(fieldName + " cannot be empty");
        }
        if (name.length() > 50) {
            throw new InvalidInputException(fieldName + " cannot exceed 50 characters");
        }
        return name.trim();
    }

    private String validatePhoneNumber(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            if (!phoneNumber.matches("^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s./0-9]*$")) {
                throw new InvalidInputException("Invalid phone number format");
            }
            if (phoneNumber.length() > 20) {
                throw new InvalidInputException("Phone number cannot exceed 20 characters");
            }
        }
        return phoneNumber;
    }

    private String encodePassword(String password) {
        if (!StringUtils.hasText(password)) {
            throw new InvalidInputException("Password cannot be empty");
        }
        if (password.length() < 8) {
            throw new InvalidInputException("Password must be at least 8 characters");
        }
        return passwordEncoder.encode(password);
    }
}