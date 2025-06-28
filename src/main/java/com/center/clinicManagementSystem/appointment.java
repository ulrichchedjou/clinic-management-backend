package com.clinic.entity;

import com.clinic.enums.AppointmentStatus;
import com.clinic.enums.AppointmentType;
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

/**
 * Entité représentant un rendez-vous médical
 */
@Entity
@Table(name = "appointments", indexes = {
    @Index(name = "idx_appointment_date", columnList = "appointment_date"),
    @Index(name = "idx_appointment_doctor", columnList = "doctor_id"),
    @Index(name = "idx_appointment_patient", columnList = "patient_id"),
    @Index(name = "idx_appointment_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_number", unique = true, nullable = false, length = 20)
    private String appointmentNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Le patient est obligatoire")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @NotNull(message = "Le docteur est obligatoire")
    private Doctor doctor;

    @Column(name = "appointment_date", nullable = false)
    @NotNull(message = "La date du rendez-vous est obligatoire")
    private LocalDateTime appointmentDate;

    @Column(name = "estimated_duration")
    @Builder.Default
    private Integer estimatedDuration = 30; // en minutes

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AppointmentType type = AppointmentType.CONSULTATION;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    @Column(name = "reason_for_visit", columnDefinition = "TEXT")
    private String reasonForVisit;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "symptoms", columnDefinition = "TEXT")
    private String symptoms;

    @Column(name = "consultation_fee", precision = 10, scale = 2)
    private BigDecimal consultationFee;

    @Column(name = "is_emergency")
    @Builder.Default
    private Boolean isEmergency = false;

    @Column(name = "is_follow_up")
    @Builder.Default
    private Boolean isFollowUp = false;

    @Column(name = "previous_appointment_id")
    private Long previousAppointmentId;

    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @Column(name = "cancelled_by", length = 100)
    private String cancelledBy;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "reminder_sent")
    @Builder.Default
    private Boolean reminderSent = false;

    @Column(name = "reminder_sent_at")
    private LocalDateTime reminderSentAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relations
    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MedicalRecord medicalRecord;

    // Méthodes utilitaires
    public boolean isScheduled() {
        return status == AppointmentStatus.SCHEDULED;
    }

    public boolean isCompleted() {
        return status == AppointmentStatus.COMPLETED;
    }

    public boolean isCancelled() {
        return status == AppointmentStatus.CANCELLED;
    }

    public boolean isNoShow() {
        return status == AppointmentStatus.NO_SHOW;
    }

    public boolean isInProgress() {
        return status == AppointmentStatus.IN_PROGRESS;
    }

    public boolean isPast() {
        return appointmentDate.isBefore(LocalDateTime.now());
    }

    public boolean isToday() {
        LocalDateTime now = LocalDateTime.now();
        return appointmentDate.toLocalDate().equals(now.toLocalDate());
    }

    public boolean isUpcoming() {
        return appointmentDate.isAfter(LocalDateTime.now());
    }

    public Integer getActualDuration() {
        if (actualStartTime != null && actualEndTime != null) {
            return (int) java.time.Duration.between(actualStartTime, actualEndTime).toMinutes();
        }
        return null;
    }

    public LocalDateTime getEstimatedEndTime() {
        return appointmentDate.plusMinutes(estimatedDuration);
    }

    public boolean canBeCancelled() {
        return status == AppointmentStatus.SCHEDULED && 
               appointmentDate.isAfter(LocalDateTime.now().plusHours(2)); // Minimum 2h avant
    }

    public boolean canBeRescheduled() {
        return status == AppointmentStatus.SCHEDULED && 
               appointmentDate.isAfter(LocalDateTime.now().plusHours(4)); // Minimum 4h avant
    }

    public String getPatientName() {
        return patient != null ? patient.getFullName() : "";
    }

    public String getDoctorName() {
        return doctor != null ? doctor.getFullName() : "";
    }
}