package com.center.clinicManagementSystem.responses;

import com.center.clinicManagementSystem.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserInfo user;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private Long id;
        private String email;
        private String firstName;
        private String lastName;
        private String fullName;
        private UserRole role;
        private Boolean emailVerified;
        private LocalDateTime lastLogin;
        private String profilePictureUrl;

        // Informations docteur si applicable
        private DoctorInfo doctor;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DoctorInfo {
        private Long id;
        private String licenseNumber;
        private String specialization;
        private String officeNumber;
        private Boolean isAvailableForEmergency;
    }
}
