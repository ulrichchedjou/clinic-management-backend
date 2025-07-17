package com.center.clinicManagementSystem.integration;

import com.center.clinicManagementSystem.ClinicManagementSystemApplication;
import com.center.clinicManagementSystem.dtos.LoginRequest;
import com.center.clinicManagementSystem.model.User;
import com.center.clinicManagementSystem.responses.JwtResponse;
import com.center.clinicManagementSystem.repository.UserRepository;
import com.center.clinicManagementSystem.enums.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = ClinicManagementSystemApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Nettoyer la base de données avant chaque test
        userRepository.deleteAll();
        
        // Créer un utilisateur de test
        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(UserRole.PATIENT);
        userRepository.save(testUser);
    }

    @Test
    void login_WithValidCredentials_ReturnsJwtToken() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andReturn();

        // Vérifier que le token JWT est valide
        String response = result.getResponse().getContentAsString();
        JwtResponse jwtResponse = objectMapper.readValue(response, JwtResponse.class);
        assertNotNull(jwtResponse.getAccessToken());
        assertNotNull(jwtResponse.getRefreshToken());
    }

    @Test
    void login_WithInvalidCredentials_ReturnsUnauthorized() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongpassword");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void forgotPassword_WithValidEmail_SendsResetEmail() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/forgot-password")
                .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Email de réinitialisation envoyé"));
    }

    @Test
    void resetPassword_WithValidToken_UpdatesPassword() throws Exception {
        // Given
        // Note: Dans un cas réel, vous devriez d'abord appeler forgot-password
        // pour générer un token valide, puis l'utiliser ici.
        // Pour ce test, nous simulons un token valide.
        
        // When & Then
        mockMvc.perform(post("/api/auth/reset-password")
                .param("token", "valid-test-token")
                .param("newPassword", "newPassword123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Mot de passe réinitialisé avec succès"));
    }
}
