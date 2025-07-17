package com.center.clinicManagementSystem.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI clinicManagementAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(
                        new Components()
                                .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                        new SecurityScheme()
                                                .name(SECURITY_SCHEME_NAME)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")))
                .info(new Info()
                        .title("Clinic Management System API")
                        .description("""
                                Documentation complète de l'API du système de gestion de clinique.
                                
                                ## Authentification
                                La plupart des endpoints nécessitent une authentification JWT. 
                                Utilisez le endpoint `/api/auth/login` pour obtenir un token.
                                
                                ## Rôles disponibles
                                - **ADMIN**: Accès complet au système
                                - **DOCTOR**: Gestion des patients et des rendez-vous
                                - **RECEPTIONIST**: Gestion des rendez-vous
                                - **PATIENT**: Accès aux informations personnelles et aux rendez-vous
                                """)
                        .version("1.0.0")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
