package com.center.clinicManagementSystem.enums;

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
