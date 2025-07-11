package com.center.clinicManagementSystem.enums;

public enum MessageStatus {
    SENT("Envoyé"),
    DELIVERED("Livré"),
    READ("Lu"),
    FAILED("Échec"),
    DELETED("Supprimé");

    private final String description;

    MessageStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
