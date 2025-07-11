package com.center.clinicManagementSystem.enums;

/**
 * Énumération des statuts matrimoniaux
 */
public enum MaritalStatus {
    SINGLE("Célibataire"),
    MARRIED("Marié(e)"),
    DIVORCED("Divorcé(e)"),
    WIDOWED("Veuf/Veuve"),
    SEPARATED("Séparé(e)");

    private final String displayName;

    MaritalStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
