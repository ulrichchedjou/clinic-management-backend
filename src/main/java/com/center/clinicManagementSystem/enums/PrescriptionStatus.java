package com.center.clinicManagementSystem.enums;

/**
 * Énumération des statuts de prescription
 */
public enum PrescriptionStatus {
    ACTIVE("Active"),
    COMPLETED("Terminée"),
    CANCELLED("Annulée"),
    EXPIRED("Expirée");

    private final String displayName;

    PrescriptionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
