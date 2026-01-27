package com.medibridge.prescription_service_medibridge.web.controller;

import com.medibridge.prescription_service_medibridge.domain.MedicineOrder;
import com.medibridge.prescription_service_medibridge.service.MedicineOrderService;
import com.medibridge.prescription_service_medibridge.util.SecurityUtils;
import com.medibridge.prescription_service_medibridge.web.dto.OrderRequest;
import com.medibridge.prescription_service_medibridge.web.dto.OrderResponse;
import com.medibridge.prescription_service_medibridge.web.mapper.OrderMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class PatientOrderController {

    private final MedicineOrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid OrderRequest request) {
        MedicineOrder order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderMapper.toResponse(order));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Page<OrderResponse>> getMyOrders(Pageable pageable) {
        String patientId = SecurityUtils.getCurrentUserId();
        Page<MedicineOrder> page = orderService.getMyOrders(patientId, pageable);
        return ResponseEntity.ok(page.map(orderMapper::toResponse));
    }
}
