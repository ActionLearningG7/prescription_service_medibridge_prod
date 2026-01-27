package com.medibridge.prescription_service_medibridge.web.controller;

import com.medibridge.prescription_service_medibridge.domain.Prescription;
import com.medibridge.prescription_service_medibridge.service.PrescriptionService;
import com.medibridge.prescription_service_medibridge.web.dto.PrescriptionRequest;
import com.medibridge.prescription_service_medibridge.web.dto.PrescriptionResponse;
import com.medibridge.prescription_service_medibridge.web.mapper.PrescriptionMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/prescriptions")
@RequiredArgsConstructor
public class DoctorPrescriptionController {

    private final PrescriptionService prescriptionService;
    private final PrescriptionMapper prescriptionMapper;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<PrescriptionResponse> createPrescription(@RequestBody @Valid PrescriptionRequest request) {
        Prescription prescription = prescriptionService.createPrescription(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(prescriptionMapper.toResponse(prescription));
    }

    @PostMapping("/{id}/issue")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Void> issuePrescription(@PathVariable String id) {
        prescriptionService.issuePrescription(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/doctor/me")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Page<PrescriptionResponse>> getMyPrescriptions(
            @RequestParam(required = false) com.medibridge.prescription_service_medibridge.domain.enums.PrescriptionStatus status,
            Pageable pageable) {
        String doctorId = com.medibridge.prescription_service_medibridge.util.SecurityUtils.getCurrentUserId();
        Page<Prescription> page = prescriptionService.getDoctorPrescriptions(doctorId, status, pageable);
        return ResponseEntity.ok(page.map(prescriptionMapper::toResponse));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<PrescriptionResponse> updatePrescription(
            @PathVariable String id,
            @RequestBody @Valid PrescriptionRequest request) {
        Prescription prescription = prescriptionService.updatePrescription(id, request);
        return ResponseEntity.ok(prescriptionMapper.toResponse(prescription));
    }
}
