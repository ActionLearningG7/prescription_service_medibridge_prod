package com.medibridge.prescription_service_medibridge.event.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class PrescriptionIssuedEvent {
    private String prescriptionId;
    private String patientId;
    private String doctorId;
    private String appointmentId;
    private LocalDateTime issuedAt;
}
