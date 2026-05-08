package com.medibridge.prescription_service_medibridge.web.controller;

import com.medibridge.prescription_service_medibridge.domain.enums.MedicineOrderStatus;
import com.medibridge.prescription_service_medibridge.domain.enums.PrescriptionStatus;
import com.medibridge.prescription_service_medibridge.domain.Prescription;
import com.medibridge.prescription_service_medibridge.service.MedicineOrderService;
import com.medibridge.prescription_service_medibridge.service.PrescriptionService;
import com.medibridge.prescription_service_medibridge.web.dto.PrescriptionResponse;
import com.medibridge.prescription_service_medibridge.web.mapper.PrescriptionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
public class AdminController {

    private final MedicineOrderService orderService;
    private final PrescriptionService prescriptionService;
    private final PrescriptionMapper prescriptionMapper;

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam MedicineOrderStatus status) {
        orderService.updateStatus(orderId, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/prescriptions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PrescriptionResponse>> getAllPrescriptions(
            @RequestParam(required = false) PrescriptionStatus status,
            Pageable pageable) {
        Page<Prescription> page = prescriptionService.getAllPrescriptions(status, pageable);
        return ResponseEntity.ok(page.map(prescriptionMapper::toResponse));
    }
}
