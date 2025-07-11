package com.center.clinicManagementSystem.enums;

public enum UserStatus {
    ACTIVE("Actif"),
    INACTIVE("Inactif"),
    SUSPENDED("Suspendu"),
    PENDING_VERIFICATION("En attente de vérification");

    private final String displayName;

    UserStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
