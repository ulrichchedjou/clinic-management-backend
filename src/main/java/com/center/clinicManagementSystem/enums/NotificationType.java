package com.center.clinicManagementSystem.enums;

public enum NotificationType {
    NEW_MESSAGE("Nouveau message"),
    USER_JOINED("Utilisateur rejoint"),
    USER_LEFT("Utilisateur quitté"),
    TYPING("En train d'écrire"),
    MESSAGE_EDITED("Message modifié"),
    MESSAGE_DELETED("Message supprimé"),
    ROOM_CREATED("Salon créé"),
    ROOM_UPDATED("Salon mis à jour"),
    APPOINTMENT_REMINDER("Rappel de rendez-vous"),
    URGENT_NOTIFICATION("Notification urgente"),
    SYSTEM_MAINTENANCE("Maintenance système");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
