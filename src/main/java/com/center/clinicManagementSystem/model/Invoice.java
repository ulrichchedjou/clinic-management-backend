package com.center.clinicManagementSystem.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Add fields as needed
}
