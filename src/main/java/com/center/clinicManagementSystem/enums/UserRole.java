package com.center.clinicManagementSystem.enums;

/**
 * Énumération des rôles utilisateur
 */
public enum UserRole {
    ADMIN("Administrateur"),
    DOCTOR("Médecin"),
    PATIENT("Patient"),
    RECEPTIONIST("Réceptionniste"),
    NURSE("Infirmier(ère)");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
