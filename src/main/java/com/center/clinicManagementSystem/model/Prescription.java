package com.center.clinicManagementSystem.model;

import com.center.clinicManagementSystem.enums.PrescriptionStatus;
import com.center.clinicManagementSystem.model.Doctor;
import com.center.clinicManagementSystem.model.MedicalRecord;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entité représentant une prescription médicale
 */
@Entity
@Table(name = "prescriptions", indexes = {
    @Index(name = "idx_prescription_patient", columnList = "patient_id"),
    @Index(name = "idx_prescription_doctor", columnList = "doctor_id"),
    @Index(name = "idx_prescription_date", columnList = "prescription_date"),
    @Index(name = "idx_prescription_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prescription_number", unique = true, nullable = false, length = 50)
    private String prescriptionNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Le patient est obligatoire")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @NotNull(message = "Le docteur est obligatoire")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id")
    private MedicalRecord medicalRecord;

    @Column(name = "prescription_date", nullable = false)
    @NotNull(message = "La date de prescription est obligatoire")
    private LocalDate prescriptionDate;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PrescriptionStatus status = PrescriptionStatus.ACTIVE;

    @Column(name = "diagnosis", columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;

    @Column(name = "is_repeat_prescription")
    @Builder.Default
    private Boolean isRepeatPrescription = false;

    @Column(name = "original_prescription_id")
    private Long originalPrescriptionId;

    @Column(name = "is_dispensed")
    @Builder.Default
    private Boolean isDispensed = false;

    @Column(name = "dispensed_date")
    private LocalDate dispensedDate;

    @Column(name = "pharmacy_name", length = 100)
    private String pharmacyName;

    @Column(name = "pharmacist_name", length = 100)
    private String pharmacistName;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relations
    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PrescriptionItem> items;

    // Méthodes utilitaires
    public String getPatientName() {
        return patient != null ? patient.getFullName() : "";
    }

    public String getDoctorName() {
        return doctor != null ? doctor.getFullName() : "";
    }

    public boolean isActive() {
        return status == PrescriptionStatus.ACTIVE;
    }

    public boolean isExpired() {
        return validUntil != null && validUntil.isBefore(LocalDate.now());
    }

    public boolean isValid() {
        return isActive() && !isExpired();
    }

    public int getTotalItems() {
        return items != null ? items.size() : 0;
    }
}

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
class PrescriptionItem {

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