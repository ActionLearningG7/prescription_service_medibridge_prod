package com.medibridge.prescription_service_medibridge.web.controller;

import com.medibridge.prescription_service_medibridge.domain.Prescription;
import com.medibridge.prescription_service_medibridge.service.PrescriptionService;
import com.medibridge.prescription_service_medibridge.util.SecurityUtils;
import com.medibridge.prescription_service_medibridge.web.dto.PrescriptionResponse;
import com.medibridge.prescription_service_medibridge.web.mapper.PrescriptionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/prescriptions")
@RequiredArgsConstructor
public class PatientPrescriptionController {

    private final PrescriptionService prescriptionService;
    private final PrescriptionMapper prescriptionMapper;

    @GetMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Page<PrescriptionResponse>> getMyPrescriptions(Pageable pageable) {
        String patientId = SecurityUtils.getCurrentUserId();
        Page<Prescription> page = prescriptionService.getMyPrescriptions(patientId, pageable);
        return ResponseEntity.ok(page.map(prescriptionMapper::toResponse));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<PrescriptionResponse> getPrescription(@PathVariable String id) {
        Prescription prescription = prescriptionService.getPrescription(id);
        String currentUserId = SecurityUtils.getCurrentUserId();

        // Ownership Check
        if (!prescription.getPatientId().equals(currentUserId) &&
                !prescription.getDoctorId().equals(currentUserId)) {
            // In real app, consider multi-doctor access logic
            throw new SecurityException("Access denied");
        }

        return ResponseEntity.ok(prescriptionMapper.toResponse(prescription));
    }
}
