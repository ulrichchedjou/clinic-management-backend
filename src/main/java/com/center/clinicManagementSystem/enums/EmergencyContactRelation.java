package com.center.clinicManagementSystem.enums;

/**
 * Énumération des relations de contact d'urgence
 */
public enum EmergencyContactRelation {
    PARENT("Parent"),
    SPOUSE("Conjoint(e)"),
    CHILD("Enfant"),
    SIBLING("Frère/Sœur"),
    FRIEND("Ami(e)"),
    OTHER("Autre");

    private final String displayName;

    EmergencyContactRelation(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
