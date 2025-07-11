package com.clinic.entity.model;

import com.clinic.enums.DoctorStatus;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Entité représentant un docteur
 */
@Entity
@Table(name = "doctors", indexes = {
    @Index(name = "idx_doctor_license", columnList = "license_number"),
    @Index(name = "idx_doctor_specialization", columnList = "specialization_id"),
    @Index(name = "idx_doctor_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @NotNull(message = "L'utilisateur est obligatoire")
    private User user;

    @NotBlank(message = "Le numéro de licence est obligatoire")
    @Column(name = "license_number", unique = true, nullable = false, length = 50)
    private String licenseNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialization_id", nullable = false)
    @NotNull(message = "La spécialisation est obligatoire")
    private Specialization specialization;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(columnDefinition = "TEXT")
    private String biography;

    @Column(name = "consultation_fee", precision = 10, scale = 2)
    private BigDecimal consultationFee;

    @Column(name = "work_start_time")
    private LocalTime workStartTime;

    @Column(name = "work_end_time")
    private LocalTime workEndTime;

    @Column(name = "lunch_start_time")
    private LocalTime lunchStartTime;

    @Column(name = "lunch_end_time")
    private LocalTime lunchEndTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private DoctorStatus status = DoctorStatus.ACTIVE;

    @Column(name = "is_available_for_emergency")
    @Builder.Default
    private Boolean isAvailableForEmergency = false;

    @Column(name = "office_number", length = 20)
    private String officeNumber;

    @Column(name = "education_details", columnDefinition = "TEXT")
    private String educationDetails;

    @Column(name = "certifications", columnDefinition = "TEXT")
    private String certifications;

    @Column(name = "languages_spoken", length = 200)
    private String languagesSpoken;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relations
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DoctorSchedule> schedules;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MedicalRecord> medicalRecords;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Prescription> prescriptions;

    // Méthodes utilitaires
    public String getFullName() {
        return user != null ? user.getFullName() : "";
    }

    public boolean isAvailable() {
        return status == DoctorStatus.ACTIVE;
    }

    public boolean isWorkingHour(LocalTime time) {
        if (workStartTime == null || workEndTime == null) {
            return true; // Pas de restriction d'horaire
        }
        
        // Vérifier si c'est pendant les heures de travail
        boolean duringWork = !time.isBefore(workStartTime) && !time.isAfter(workEndTime);
        
        // Vérifier si c'est pendant la pause déjeuner
        boolean duringLunch = false;
        if (lunchStartTime != null && lunchEndTime != null) {
            duringLunch = !time.isBefore(lunchStartTime) && !time.isAfter(lunchEndTime);
        }
        
        return duringWork && !duringLunch;
    }
}