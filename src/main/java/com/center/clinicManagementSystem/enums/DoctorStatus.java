package com.center.clinicManagementSystem.enums;

/**
 * Énumération des statuts de docteur
 */
public enum DoctorStatus {
    ACTIVE("Actif"),
    INACTIVE("Inactif"),
    ON_LEAVE("En congé"),
    SUSPENDED("Suspendu");

    private final String displayName;

    DoctorStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
