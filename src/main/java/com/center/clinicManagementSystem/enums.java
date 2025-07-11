package com.clinic.enums;

/**
 * Énumération des rôles utilisateur
 */
public enum UserRole {
    ADMIN("Administrateur"),
    DOCTOR("Médecin"),
    PATIENT("Patient"),
    RECEPTIONIST("Réceptionniste"),
    NURSE("Infirmier(ère)");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

/**
 * Énumération des statuts utilisateur
 */
public enum UserStatus {
    ACTIVE("Actif"),
    INACTIVE("Inactif"),
    SUSPENDED("Suspendu"),
    PENDING_VERIFICATION("En attente de vérification");

    private final String displayName;

    UserStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

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

/**
 * Énumération des statuts de docteur
 */
public enum DoctorStatus {
    ACTIVE("Actif"),
    INACTIVE("Inactif"),
    ON_LEAVE("En congé"),
    SUSPENDED("Suspendu");

    private final String displayName;

    DoctorStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

/**
 * Énumération des groupes sanguins
 */
public enum BloodType {
    A_POSITIVE("A+"),
    A_NEGATIVE("A-"),
    B_POSITIVE("B+"),
    B_NEGATIVE("B-"),
    AB_POSITIVE("AB+"),
    AB_NEGATIVE("AB-"),
    O_POSITIVE("O+"),
    O_NEGATIVE("O-");

    private final String displayName;

    BloodType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

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

/**
 * Énumération des statuts de prescription
 */
public enum PrescriptionStatus {
    ACTIVE("Active"),
    COMPLETED("Terminée"),
    CANCELLED("Annulée"),
    EXPIRED("Expirée");

    private final String displayName;

    PrescriptionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

/**
 * Énumération des statuts de facture
 */
public enum InvoiceStatus {
    DRAFT("Brouillon"),
    PENDING("En attente"),
    PAID("Payée"),
    OVERDUE("En retard"),
    CANCELLED("Annulée"),
    REFUNDED("Remboursée");

    private final String displayName;

    InvoiceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

/**
 * Énumération des méthodes de paiement
 */
public enum PaymentMethod {
    CASH("Espèces"),
    CARD("Carte"),
    BANK_TRANSFER("Virement bancaire"),
    MOBILE_MONEY("Mobile Money"),
    INSURANCE("Assurance"),
    OTHER("Autre");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

/**
 * Énumération des jours de la semaine
 */
public enum DayOfWeek {
    MONDAY("Lundi"),
    TUESDAY("Mardi"),
    WEDNESDAY("Mercredi"),
    THURSDAY("Jeudi"),
    FRIDAY("Vendredi"),
    SATURDAY("Samedi"),
    SUNDAY("Dimanche");

    private final String displayName;

    DayOfWeek(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

public enum ChatRoomType {
    PRIVATE("Privé"),
    GROUP("Groupe"),
    CONSULTATION("Consultation"),
    EMERGENCY("Urgence");
    
    private final String displayName;
    
    ChatRoomType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

public enum MessageType {
    TEXT("Texte"),
    IMAGE("Image"),
    FILE("Fichier"),
    AUDIO("Audio"),
    VIDEO("Vidéo"),
    SYSTEM("Système");
    
    private final String displayName;
    
    MessageType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

public enum MessageStatus {
    SENT("Envoyé"),
    DELIVERED("Livré"),
    READ("Lu"),
    FAILED("Échec");
    
    private final String displayName;
    
    MessageStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

public enum ParticipantRole {
    ADMIN("Administrateur"),
    MODERATOR("Modérateur"),
    MEMBER("Membre");
    
    private final String displayName;
    
    ParticipantRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}