package com.center.clinicManagementSystem.enums;

/**
 * Énumération des types de rendez-vous
 */
public enum AppointmentType {
    CONSULTATION("Consultation"),
    FOLLOW_UP("Suivi"),
    EMERGENCY("Urgence"),
    SURGERY("Chirurgie"),
    CHECKUP("Contrôle"),
    VACCINATION("Vaccination"),
    LABORATORY("Laboratoire"),
    RADIOLOGY("Radiologie");

    private final String displayName;

    AppointmentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
