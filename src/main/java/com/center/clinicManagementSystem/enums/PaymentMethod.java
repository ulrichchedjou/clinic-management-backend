package com.center.clinicManagementSystem.enums;

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
