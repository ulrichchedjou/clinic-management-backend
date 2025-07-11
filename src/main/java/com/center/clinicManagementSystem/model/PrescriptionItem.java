package com.center.clinicManagementSystem.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entité représentant un élément de prescription (médicament)
 */
@Entity
@Table(name = "prescription_items")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id", nullable = false)
    private Medication medication;

    @NotBlank(message = "La posologie est obligatoire")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String dosage;

    @NotBlank(message = "La fréquence est obligatoire")
    @Column(nullable = false, length = 100)
    private String frequency;

    @Column(nullable = false)
    @NotNull(message = "La durée est obligatoire")
    private Integer duration;

    @Column(name = "duration_unit", length = 20)
    @Builder.Default
    private String durationUnit = "days";

    @Column(nullable = false)
    @NotNull(message = "La quantité est obligatoire")
    private Integer quantity;

    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;

    @Column(name = "is_substitution_allowed")
    @Builder.Default
    private Boolean isSubstitutionAllowed = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Méthodes utilitaires
    public String getMedicationName() {
        return medication != null ? medication.getName() : "";
    }

    public String getFullDosageInfo() {
        return dosage + " - " + frequency + " pendant " + duration + " " + durationUnit;
    }
}

