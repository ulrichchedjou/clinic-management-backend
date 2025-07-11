package com.center.clinicManagementSystem.enums;

public enum MessageType {
    TEXT("Message texte"),
    IMAGE("Image"),
    FILE("Fichier"),
    VIDEO("Vidéo"),
    AUDIO("Audio"),
    DOCUMENT("Document"),
    SYSTEM("Message système"),
    NOTIFICATION("Notification"),
    APPOINTMENT_REQUEST("Demande de rendez-vous"),
    APPOINTMENT_CONFIRMATION("Confirmation de rendez-vous"),
    URGENT("Message urgent"),
    PRESCRIPTION("Prescription"),
    MEDICAL_REPORT("Rapport médical");

    private final String description;

    MessageType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
