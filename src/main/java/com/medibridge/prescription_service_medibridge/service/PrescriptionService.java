package com.medibridge.prescription_service_medibridge.service;

import com.medibridge.prescription_service_medibridge.domain.Prescription;
import com.medibridge.prescription_service_medibridge.domain.enums.PrescriptionStatus;
import com.medibridge.prescription_service_medibridge.event.KafkaProducer;
import com.medibridge.prescription_service_medibridge.event.dto.PrescriptionIssuedEvent;
import com.medibridge.prescription_service_medibridge.repository.PrescriptionRepository;
import com.medibridge.prescription_service_medibridge.util.SecurityUtils;
import com.medibridge.prescription_service_medibridge.web.dto.PrescriptionRequest;
import com.medibridge.prescription_service_medibridge.web.mapper.PrescriptionMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionMapper prescriptionMapper;
    private final KafkaProducer kafkaProducer;
    // private final AppointmentClient appointmentClient; // Uncomment when
    // integration is ready

    @Transactional
    public Prescription createPrescription(PrescriptionRequest request) {
        String currentDoctorId = SecurityUtils.getCurrentUserId();

        // 1. Verify Appointment Ownership (Mocked for now, assumes Client works)
        // if (!appointmentClient.verifyAppointmentDoctor(request.getAppointmentId(),
        // currentDoctorId)) {
        // throw new IllegalArgumentException("Doctor is not assigned to this
        // appointment");
        // }

        // 2. Check for duplicate prescription for this appointment
        if (prescriptionRepository.existsByAppointmentId(request.getAppointmentId())) {
            throw new IllegalStateException("Prescription already exists for this appointment");
        }

        // 3. Create Entity
        Prescription prescription = prescriptionMapper.toEntity(request);
        prescription.setDoctorId(currentDoctorId);
        prescription.setPrescriptionNumber("RX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        prescription.setStatus(request.isIssueImmediately() ? PrescriptionStatus.ISSUED : PrescriptionStatus.DRAFT);

        if (request.isIssueImmediately()) {
            prescription.setIssuedAt(LocalDateTime.now());
        }

        // 4. Handle Medications (bi-directional link)
        if (request.getMedications() != null) {
            request.getMedications().forEach(medDto -> {
                prescription.addMedication(prescriptionMapper.toMedicationEntity(medDto));
            });
        }

        Prescription saved = prescriptionRepository.save(prescription);

        // 5. Emit Event if Issued
        if (saved.getStatus() == PrescriptionStatus.ISSUED) {
            emitPrescriptionIssuedEvent(saved);
        }

        return saved;
    }

    @Transactional(readOnly = true)
    public Prescription getPrescription(String id) {
        return prescriptionRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("Prescription not found"));
    }

    @Transactional(readOnly = true)
    public Page<Prescription> getMyPrescriptions(String patientId, Pageable pageable) {
        return prescriptionRepository.findByPatientId(patientId, pageable);
    }

    @Transactional
    public void issuePrescription(String id) {
        Prescription prescription = getPrescription(id);

        // Verify ownership
        String currentDoctorId = SecurityUtils.getCurrentUserId();
        if (!prescription.getDoctorId().equals(currentDoctorId)) {
            throw new SecurityException("Not authorized to issue this prescription");
        }

        if (prescription.getStatus() != PrescriptionStatus.DRAFT) {
            throw new IllegalStateException("Prescription is already issued or cancelled");
        }

        prescription.setStatus(PrescriptionStatus.ISSUED);
        prescription.setIssuedAt(LocalDateTime.now());

        prescriptionRepository.save(prescription);
        emitPrescriptionIssuedEvent(prescription);
    }

    @Transactional(readOnly = true)
    public Page<Prescription> getDoctorPrescriptions(String doctorId, PrescriptionStatus status, Pageable pageable) {
        if (status != null) {
            return prescriptionRepository.findByDoctorIdAndStatus(doctorId, status, pageable);
        }
        return prescriptionRepository.findByDoctorId(doctorId, pageable);
    }

    @Transactional
    public Prescription updatePrescription(String id, PrescriptionRequest request) {
        Prescription prescription = getPrescription(id);

        // Verify ownership
        String currentDoctorId = SecurityUtils.getCurrentUserId();
        if (!prescription.getDoctorId().equals(currentDoctorId)) {
            throw new SecurityException("Not authorized to update this prescription");
        }

        if (prescription.getStatus() != PrescriptionStatus.DRAFT) {
            throw new IllegalStateException("Cannot update an issued or cancelled prescription");
        }

        // Update fields
        prescription.setDiagnosisSummary(request.getDiagnosisSummary());
        prescription.setNotesToPatient(request.getNotesToPatient());
        prescription.setNotesToPharmacist(request.getNotesToPharmacist());
        prescription.setFollowUpDate(request.getFollowUpDate());

        // Handle Issue Immediately
        if (request.isIssueImmediately()) {
            prescription.setStatus(PrescriptionStatus.ISSUED);
            prescription.setIssuedAt(LocalDateTime.now());
        }

        // Update Medications
        if (request.getMedications() != null) {
            // Clear existing (orphan removal should handle deletion if configured)
            prescription.getMedications().clear();

            // Add new
            request.getMedications().forEach(medDto -> {
                prescription.addMedication(prescriptionMapper.toMedicationEntity(medDto));
            });
        }

        Prescription saved = prescriptionRepository.save(prescription);

        if (saved.getStatus() == PrescriptionStatus.ISSUED) {
            emitPrescriptionIssuedEvent(saved);
        }

        return saved;
    }

    private void emitPrescriptionIssuedEvent(Prescription p) {
        PrescriptionIssuedEvent event = PrescriptionIssuedEvent.builder()
                .prescriptionId(p.getId().toString())
                .patientId(p.getPatientId())
                .doctorId(p.getDoctorId())
                .appointmentId(p.getAppointmentId())
                .issuedAt(p.getIssuedAt())
                .build();

        kafkaProducer.publish("prescription-issued", event);
    }
}
