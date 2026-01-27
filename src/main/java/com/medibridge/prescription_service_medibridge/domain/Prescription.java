package com.medibridge.prescription_service_medibridge.domain;

import com.medibridge.prescription_service_medibridge.domain.enums.PrescriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "prescriptions", indexes = {
        @Index(name = "idx_prescription_number", columnList = "prescriptionNumber", unique = true),
        @Index(name = "idx_patient_id", columnList = "patientId"),
        @Index(name = "idx_doctor_id", columnList = "doctorId"),
        @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String prescriptionNumber;

    @Column(nullable = false, length = 36)
    private String appointmentId;

    @Column(nullable = false, length = 36)
    private String patientId;

    @Column(nullable = false, length = 36)
    private String doctorId;

    private LocalDateTime issuedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrescriptionStatus status;

    @Column(columnDefinition = "TEXT")
    private String diagnosisSummary;

    @Column(columnDefinition = "TEXT")
    private String notesToPatient;

    @Column(columnDefinition = "TEXT")
    private String notesToPharmacist;

    private LocalDateTime followUpDate;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PrescriptionMedication> medications = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(length = 36)
    private String createdBy; // User ID of creator (usually doctor)

    @Version
    private Long version; // Optimistic locking

    // Helper methods
    public void addMedication(PrescriptionMedication medication) {
        medications.add(medication);
        medication.setPrescription(this);
    }

    public void removeMedication(PrescriptionMedication medication) {
        medications.remove(medication);
        medication.setPrescription(null);
    }
}
