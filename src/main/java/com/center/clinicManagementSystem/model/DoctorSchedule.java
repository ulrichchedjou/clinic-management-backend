package com.center.clinicManagementSystem.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class DoctorSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Add fields as needed
}
