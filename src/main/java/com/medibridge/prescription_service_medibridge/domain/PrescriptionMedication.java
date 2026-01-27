package com.medibridge.prescription_service_medibridge.domain;

import com.medibridge.prescription_service_medibridge.domain.enums.MedicineRoute;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "prescription_medications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionMedication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @Column(nullable = false)
    private String medicineName;

    // Internal code (optional, can be null if custom med)
    private String medicineCode;

    @Column(nullable = false)
    private String dosage; // e.g. "500mg"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicineRoute route;

    @Column(nullable = false)
    private String frequency; // e.g. "1-0-1"

    @Column(nullable = false)
    private Integer durationDays;

    @Column(nullable = false)
    private Integer quantity; // Total pills/units

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Builder.Default
    private boolean substitutionAllowed = false;
}
