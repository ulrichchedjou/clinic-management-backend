package com.center.clinicManagementSystem.enums;

/**
 * Énumération des genres
 */
public enum Gender {
    MALE("Masculin"),
    FEMALE("Féminin"),
    OTHER("Autre");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
