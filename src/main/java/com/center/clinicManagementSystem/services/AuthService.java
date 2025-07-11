package com.center.clinicManagementSystem.services;

import com.center.clinicManagementSystem.dtos.LoginRequest;
import com.center.clinicManagementSystem.dtos.RefreshTokenRequest;
import com.center.clinicManagementSystem.exceptions.AuthenticationException;
import com.center.clinicManagementSystem.exceptions.TokenException;
import com.center.clinicManagementSystem.exceptions.UserNotFoundException;
import com.center.clinicManagementSystem.model.User;
import com.center.clinicManagementSystem.model.RefreshToken;
import com.center.clinicManagementSystem.repository.UserRepository;
import com.center.clinicManagementSystem.repository.RefreshTokenRepository;
import com.center.clinicManagementSystem.responses.JwtResponse;
import com.center.clinicManagementSystem.securityConfig.JwtUtils;
import com.center.clinicManagementSystem.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final EmailService emailService;

    @Value("${app.password-reset.expiration:3600000}") // 1 hour
    private long passwordResetExpiration;

    @Transactional
    public JwtResponse login(LoginRequest loginRequest) {
        try {
            // Authentification
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Récupération de l'utilisateur
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));

            // Vérification de l'état du compte
            if (!user.isEnabled()) {
                throw new DisabledException("Compte désactivé");
            }

            // Génération des tokens
            String accessToken = jwtUtils.generateJwtToken(authentication);
            String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

            // Sauvegarde du refresh token
            saveRefreshToken(user, refreshToken, loginRequest.getRememberMe());

            // Mise à jour de la dernière connexion
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            // Construction de la réponse
            JwtResponse jwtResponse = buildJwtResponse(user, accessToken, refreshToken);

            log.info("User {} logged in successfully", user.getEmail());
            return jwtResponse;

        } catch (BadCredentialsException e) {
            log.warn("Failed login attempt for email: {}", loginRequest.getEmail());
            throw new AuthenticationException("Email ou mot de passe incorrect");
        } catch (DisabledException e) {
            log.warn("Login attempt for disabled account: {}", loginRequest.getEmail());
            throw new AuthenticationException("Compte désactivé");
        }
    }

    @Transactional
    public JwtResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        // Validation du refresh token
        if (!jwtUtils.validateJwtToken(requestRefreshToken) || !jwtUtils.isRefreshToken(requestRefreshToken)) {
            throw new TokenException("Refresh token invalide");
        }

        String email = jwtUtils.getUserNameFromJwtToken(requestRefreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));

        // Vérification de l'existence du refresh token en base
        RefreshToken storedToken = refreshTokenRepository.findByTokenAndUser(requestRefreshToken, user)
                .orElseThrow(() -> new TokenException("Refresh token non trouvé"));

        if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new TokenException("Refresh token expiré");
        }

        // Génération d'un nouveau access token
        UserDetails userDetails = userService.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        String newAccessToken = jwtUtils.generateJwtToken(authentication);

        // Optionnel : génération d'un nouveau refresh token
        String newRefreshToken = jwtUtils.generateRefreshToken(email);
        storedToken.setToken(newRefreshToken);
        storedToken.setExpiryDate(LocalDateTime.now().plusSeconds(jwtUtils.getRefreshTokenExpirationMs() / 1000));
        refreshTokenRepository.save(storedToken);

        return buildJwtResponse(user, newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (jwtUtils.validateJwtToken(refreshToken)) {
            String email = jwtUtils.getUserNameFromJwtToken(refreshToken);
            User user = userRepository.findByEmail(email).orElse(null);

            if (user != null) {
                refreshTokenRepository.deleteByTokenAndUser(refreshToken, user);
                log.info("User {} logged out successfully", email);
            }
        }
    }

    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Aucun compte associé à cet email"));

        // Génération d'un token de réinitialisation
        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetExpiry(LocalDateTime.now().plusSeconds(passwordResetExpiration / 1000));
        userRepository.save(user);

        // Envoi de l'email (à implémenter selon votre service d'email)
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);

        log.info("Password reset email sent to {}", email);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new TokenException("Token de réinitialisation invalide"));

        if (user.getPasswordResetExpiry().isBefore(LocalDateTime.now())) {
            throw new TokenException("Token de réinitialisation expiré");
        }

        // Mise à jour du mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpiry(null);
        userRepository.save(user);

        // Suppression de tous les refresh tokens de l'utilisateur
        refreshTokenRepository.deleteByUser(user);

        log.info("Password reset successfully for user {}", user.getEmail());
    }

    private void saveRefreshToken(User user, String token, Boolean rememberMe) {
        // Suppression des anciens refresh tokens
        refreshTokenRepository.deleteByUser(user);

        // Création du nouveau refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusSeconds(jwtUtils.getRefreshTokenExpirationMs() / 1000))
                .rememberMe(rememberMe != null ? rememberMe : false)
                .build();

        refreshTokenRepository.save(refreshToken);
    }

    private JwtResponse buildJwtResponse(User user, String accessToken, String refreshToken) {
        JwtResponse.UserInfo userInfo = JwtResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .role(user.getRole())
                .emailVerified(user.isEmailVerified())
                .lastLogin(user.getLastLogin())
                .profilePictureUrl(user.getProfilePictureUrl())
                .build();

        // Ajout des informations docteur si applicable
        if (user.getRole() == UserRole.DOCTOR && user.getDoctor() != null) {
            JwtResponse.DoctorInfo doctorInfo = JwtResponse.DoctorInfo.builder()
                    .id(user.getDoctor().getId())
                    .licenseNumber(user.getDoctor().getLicenseNumber())
                    .officeNumber(user.getDoctor().getOfficeNumber())
                    .isAvailableForEmergency(user.getDoctor().getIsAvailableForEmergency())
                    .build();
            userInfo.setDoctor(doctorInfo);
        }

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtils.getJwtExpirationMs() / 1000)
                .user(userInfo)
                .build();
    }
}