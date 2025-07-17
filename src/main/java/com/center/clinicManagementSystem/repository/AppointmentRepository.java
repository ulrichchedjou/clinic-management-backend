package com.center.clinicManagementSystem.repository;

import com.center.clinicManagementSystem.model.Appointment;
import com.center.clinicManagementSystem.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    // Find all appointments for a specific patient
    List<Appointment> findByPatientIdOrderByAppointmentDateDesc(Long patientId);
    
    // Find all appointments for a specific doctor
    List<Appointment> findByDoctorIdOrderByAppointmentDateDesc(Long doctorId);
    
    // Find appointments between two dates
    List<Appointment> findByAppointmentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find appointments by status
    List<Appointment> findByStatusOrderByAppointmentDateAsc(AppointmentStatus status);
    
    // Find appointments between two dates
    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(
            LocalDateTime startDate, LocalDateTime endDate);
    
    // Find upcoming appointments for a patient
    List<Appointment> findByPatientIdAndAppointmentDateAfterOrderByAppointmentDateAsc(
            Long patientId, LocalDateTime date);
    
    // Find upcoming appointments for a doctor
    List<Appointment> findByDoctorIdAndAppointmentDateAfterOrderByAppointmentDateAsc(
            Long doctorId, LocalDateTime date);
    
    // Find appointments by doctor and status
    List<Appointment> findByDoctorIdAndStatusOrderByAppointmentDateAsc(
            Long doctorId, AppointmentStatus status);
    
    // Find appointments by patient and status
    List<Appointment> findByPatientIdAndStatusOrderByAppointmentDateAsc(
            Long patientId, AppointmentStatus status);
    
    // Find today's appointments for a doctor
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId " +
           "AND DATE(a.appointmentDate) = CURRENT_DATE " +
           "ORDER BY a.appointmentDate ASC")
    List<Appointment> findTodaysAppointmentsByDoctor(@Param("doctorId") Long doctorId);
    
    // Find overlapping appointments for a doctor
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId " +
           "AND a.status = 'SCHEDULED' " +
           "AND a.appointmentDate < :endTime " +
           "AND a.appointmentDate + FUNCTION('MINUTE', a.estimatedDuration) > :startTime")
    List<Appointment> findOverlappingAppointments(
            @Param("doctorId") Long doctorId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    // Count appointments by status
    long countByStatus(AppointmentStatus status);
    
    // Count appointments by doctor and status
    long countByDoctorIdAndStatus(Long doctorId, AppointmentStatus status);
}
