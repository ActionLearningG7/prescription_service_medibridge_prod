package com.medibridge.prescription_service_medibridge.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PrescriptionRequest {

    @NotBlank(message = "Patient ID is required")
    private String patientId;

    @NotBlank(message = "Appointment ID is required")
    private String appointmentId;

    private String diagnosisSummary;
    private String notesToPatient;
    private String notesToPharmacist;
    private LocalDate followUpDate;

    @NotEmpty(message = "Medications list cannot be empty")
    @Valid
    private List<PrescriptionMedicationDto> medications;

    private boolean issueImmediately; // If true, sets status to ISSUED
}
