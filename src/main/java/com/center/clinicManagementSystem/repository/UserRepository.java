package com.center.clinicManagementSystem.repository;

import com.center.clinicManagementSystem.model.User;
import com.center.clinicManagementSystem.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndEnabled(String email, Boolean enabled);

    Optional<User> findByPasswordResetToken(String token);

    Optional<User> findByEmailVerificationToken(String token);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.enabled = true")
    List<User> findByRoleAndEnabled(@Param("role") UserRole role);

    @Query("SELECT u FROM User u WHERE u.enabled = true AND u.emailVerified = true")
    List<User> findActiveUsers();

    @Query("SELECT u FROM User u WHERE u.lastLogin < :date")
    List<User> findUsersNotLoggedInSince(@Param("date") LocalDateTime date);

    @Query("SELECT u FROM User u WHERE u.accountLockedUntil IS NOT NULL AND u.accountLockedUntil > :now")
    List<User> findLockedUsers(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.enabled = true")
    Long countByRoleAndEnabled(@Param("role") UserRole role);
    
    @Query("SELECT u FROM User u WHERE u.lastLogin BETWEEN :start AND :end")
    List<User> findByLastLoginBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :start AND :end")
    Long countByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
