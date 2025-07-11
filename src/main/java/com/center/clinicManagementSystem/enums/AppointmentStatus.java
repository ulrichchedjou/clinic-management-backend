package com.center.clinicManagementSystem.enums;

/**
 * Énumération des statuts de rendez-vous
 */
public enum AppointmentStatus {
    SCHEDULED("Programmé"),
    CONFIRMED("Confirmé"),
    IN_PROGRESS("En cours"),
    COMPLETED("Terminé"),
    CANCELLED("Annulé"),
    NO_SHOW("Absent"),
    RESCHEDULED("Reprogrammé");

    private final String displayName;

    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
