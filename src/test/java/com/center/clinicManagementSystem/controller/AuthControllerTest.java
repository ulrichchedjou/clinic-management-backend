package com.center.clinicManagementSystem.controller;

import com.center.clinicManagementSystem.dtos.LoginRequest;
import com.center.clinicManagementSystem.dtos.RefreshTokenRequest;
import com.center.clinicManagementSystem.responses.ApiResponse;
import com.center.clinicManagementSystem.responses.JwtResponse;
import com.center.clinicManagementSystem.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void login_WithValidCredentials_ReturnsJwtResponse() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        JwtResponse jwtResponse = new JwtResponse("accessToken", "refreshToken");
        when(authService.login(any(LoginRequest.class))).thenReturn(jwtResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.data.refreshToken").value("refreshToken"));
    }

    @Test
    void logout_WithValidToken_ReturnsSuccess() throws Exception {
        // Given
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("refreshToken");
        
        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Déconnexion réussie"));
    }

    @Test
    void forgotPassword_WithValidEmail_ReturnsSuccess() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/forgot-password")
                .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Email de réinitialisation envoyé"));
    }

    @Test
    void resetPassword_WithValidToken_ReturnsSuccess() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/reset-password")
                .param("token", "valid-token")
                .param("newPassword", "newPassword123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Mot de passe réinitialisé avec succès"));
    }
}
