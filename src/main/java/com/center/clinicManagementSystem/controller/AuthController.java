package com.center.clinicManagementSystem.controller;

import com.center.clinicManagementSystem.dtos.LoginRequest;
import com.center.clinicManagementSystem.dtos.RefreshTokenRequest;
import com.center.clinicManagementSystem.responses.ApiResponse;
import com.center.clinicManagementSystem.responses.JwtResponse;
import com.center.clinicManagementSystem.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Data
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Controller", description = "Authentication and Authorization API")
public class AuthController {

    private final AuthService authService;

        @Operation(
        summary = "Authentifier un utilisateur",
        description = "Authentifie un utilisateur avec son email et son mot de passe et retourne un JWT"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authentification réussie",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide"),
        @ApiResponse(responseCode = "401", description = "Authentification échouée"),
        @ApiResponse(responseCode = "423", description = "Compte verrouillé")
    })
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<JwtResponse>> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Informations de connexion",
                required = true,
                content = @Content(schema = @Schema(implementation = LoginRequest.class))
            )
            @Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(jwtResponse, "Connexion réussie"));
    }

    /*@PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return ResponseEntity.ok(ApiResponse.success(null, "Compte créé avec succès"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<JwtResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        JwtResponse jwtResponse = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(jwtResponse, "Token actualisé avec succès"));
    }*/

    @Operation(
        summary = "Déconnexion",
        description = "Invalide le token de rafraîchissement pour déconnecter l'utilisateur"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Déconnexion réussie"),
        @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<String>> logout(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Token de rafraîchissement à invalider",
                required = true,
                content = @Content(schema = @Schema(implementation = RefreshTokenRequest.class))
            )
            @Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Déconnexion réussie"));
    }

    /*@PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success(null, "Email vérifié avec succès"));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<String>> resendVerification(@RequestParam String email) {
        authService.resendVerificationEmail(email);
        return ResponseEntity.ok(ApiResponse.success(null, "Email de vérification envoyé"));
    }*/

    @Operation(
        summary = "Demande de réinitialisation de mot de passe",
        description = "Envoie un email avec un lien de réinitialisation de mot de passe"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email de réinitialisation envoyé"),
        @ApiResponse(responseCode = "400", description = "Email invalide")
    })
    @PostMapping(value = "/forgot-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Parameter(description = "Email de l'utilisateur", required = true)
            @RequestParam String email) {
        authService.forgotPassword(email);
        return ResponseEntity.ok(ApiResponse.success(null, "Email de réinitialisation envoyé"));
    }

    @Operation(
        summary = "Réinitialiser le mot de passe",
        description = "Réinitialise le mot de passe avec le token de réinitialisation"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mot de passe réinitialisé avec succès"),
        @ApiResponse(responseCode = "400", description = "Token invalide ou expiré")
    })
    @PostMapping(value = "/reset-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Parameter(description = "Token de réinitialisation reçu par email", required = true)
            @RequestParam String token,
            @Parameter(description = "Nouveau mot de passe (minimum 8 caractères)", required = true)
            @RequestParam String newPassword) {
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok(ApiResponse.success(null, "Mot de passe réinitialisé avec succès"));
    }
}