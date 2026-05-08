package com.medibridge.prescription_service_medibridge.web.dto;

import com.medibridge.prescription_service_medibridge.domain.enums.PrescriptionStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PrescriptionResponse {
    private String id;
    private String prescriptionNumber;
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private LocalDateTime issuedAt;
    private PrescriptionStatus status;
    private String diagnosisSummary;
    private String notesToPatient;
    private String notesToPharmacist;
    private LocalDate followUpDate;
    private List<PrescriptionMedicationDto> medications;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
