package com.medibridge.prescription_service_medibridge.web.controller;

import com.medibridge.prescription_service_medibridge.domain.enums.MedicineOrderStatus;
import com.medibridge.prescription_service_medibridge.service.MedicineOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
public class AdminController {

    private final MedicineOrderService orderService;

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam MedicineOrderStatus status) {
        orderService.updateStatus(orderId, status);
        return ResponseEntity.ok().build();
    }
}
