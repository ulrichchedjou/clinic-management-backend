package com.center.clinicManagementSystem.enums;

public enum ChatRoomType {
    PRIVATE("Conversation privée"),
    GROUP("Groupe"),
    DEPARTMENT("Département"),
    EMERGENCY("Urgence"),
    MEDICAL_TEAM("Équipe médicale"),
    ADMINISTRATIVE("Administratif"),
    PATIENT_CARE("Soins aux patients");

    private final String description;

    ChatRoomType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
