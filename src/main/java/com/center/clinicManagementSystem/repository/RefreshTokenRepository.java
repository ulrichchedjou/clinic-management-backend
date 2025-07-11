package com.center.clinicManagementSystem.repository;

import com.center.clinicManagementSystem.model.RefreshToken;
import com.center.clinicManagementSystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByTokenAndUser(String token, User user);

    List<RefreshToken> findByUser(User user);

    @Modifying
    @Transactional
    void deleteByToken(String token);

    @Modifying
    @Transactional
    void deleteByUser(User user);

    @Modifying
    @Transactional
    void deleteByTokenAndUser(String token, User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.user = :user")
    Long countByUser(@Param("user") User user);

    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user = :user ORDER BY rt.createdAt DESC")
    List<RefreshToken> findByUserOrderByCreatedAtDesc(@Param("user") User user);
}

