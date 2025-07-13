package com.mate.demo.repository;

import com.mate.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Basic CRUD operations are inherited from JpaRepository
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Find user by email (for authentication)
    Optional<User> findByEmail(String email);
    
    // Search users with pagination
    Page<User> findByEmailContainingOrFirstNameContainingOrLastNameContaining(
        String email, String firstName, String lastName, Pageable pageable);
    
    // Find all enabled/disabled users
    Page<User> findByEnabled(boolean enabled, Pageable pageable);
    
    // Find users by role
    Page<User> findByRole(User.Role role, Pageable pageable);
    
    // Count users by status
    long countByEnabled(boolean enabled);
}