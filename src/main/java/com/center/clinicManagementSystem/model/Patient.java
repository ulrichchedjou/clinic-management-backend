package com.center.clinicManagementSystem.model;

import com.center.clinicManagementSystem.enums.BloodType;
import com.center.clinicManagementSystem.enums.EmergencyContactRelation;
import com.center.clinicManagementSystem.enums.MaritalStatus;
import com.center.clinicManagementSystem.model.Appointment;
import com.center.clinicManagementSystem.model.MedicalRecord;
import com.center.clinicManagementSystem.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entité représentant un patient
 */
@Entity
@Table(name = "patients", indexes = {
    @Index(name = "idx_patient_number", columnList = "patient_number"),
    @Index(name = "idx_patient_blood_type", columnList = "blood_type")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @NotNull(message = "L'utilisateur est obligatoire")
    private User user;

    @Column(name = "patient_number", unique = true, nullable = false, length = 20)
    private String patientNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_type", length = 5)
    private BloodType bloodType;

    @Column(precision = 5, scale = 2)
    private BigDecimal height; // en mètres

    @Column(precision = 5, scale = 2)
    private BigDecimal weight; // en kg

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status", length = 20)
    private MaritalStatus maritalStatus;

    @Column(length = 100)
    private String occupation;

    @Column(name = "insurance_number", length = 50)
    private String insuranceNumber;

    @Column(name = "insurance_provider", length = 100)
    private String insuranceProvider;

    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "emergency_contact_relation", length = 20)
    private EmergencyContactRelation emergencyContactRelation;

    @Column(name = "known_allergies", columnDefinition = "TEXT")
    private String knownAllergies;

    @Column(name = "chronic_conditions", columnDefinition = "TEXT")
    private String chronicConditions;

    @Column(name = "current_medications", columnDefinition = "TEXT")
    private String currentMedications;

    @Column(name = "medical_history", columnDefinition = "TEXT")
    private String medicalHistory;

    @Column(name = "family_medical_history", columnDefinition = "TEXT")
    private String familyMedicalHistory;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Column(name = "last_visit_date")
    private LocalDateTime lastVisitDate;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relations
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MedicalRecord> medicalRecords;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Prescription> prescriptions;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Invoice> invoices;

    /*@OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VitalSigns> vitalSigns;*/

    // Méthodes utilitaires
    public String getFullName() {
        return user != null ? user.getFullName() : "";
    }

    public Integer getAge() {
        if (user == null || user.getDateOfBirth() == null) {
            return null;
        }
        return LocalDateTime.now().getYear() - user.getDateOfBirth().getYear();
    }

    public BigDecimal getBMI() {
        if (height == null || weight == null || height.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return weight.divide(height.multiply(height), 2, BigDecimal.ROUND_HALF_UP);
    }

    public String getBMICategory() {
        BigDecimal bmi = getBMI();
        if (bmi == null) {
            return "Inconnu";
        }
        if (bmi.compareTo(new BigDecimal("18.5")) < 0) {
            return "Insuffisance pondérale";
        } else if (bmi.compareTo(new BigDecimal("25")) < 0) {
            return "Poids normal";
        } else if (bmi.compareTo(new BigDecimal("30")) < 0) {
            return "Surpoids";
        } else {
            return "Obésité";
        }
    }

}
