package com.center.clinicManagementSystem.enums;

public enum ParticipantRole {
    ADMIN("Administrateur"),
    MODERATOR("Modérateur"),
    MEMBER("Membre"),
    OBSERVER("Observateur"),
    GUEST("Invité");

    private final String description;

    ParticipantRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
