package com.center.clinicManagementSystem.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Medication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    // Add more fields as needed
}
