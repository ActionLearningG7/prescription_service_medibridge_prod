package com.medibridge.prescription_service_medibridge.service;

import com.medibridge.prescription_service_medibridge.domain.MedicineOrder;
import com.medibridge.prescription_service_medibridge.domain.MedicineOrderItem;
import com.medibridge.prescription_service_medibridge.domain.Prescription;
import com.medibridge.prescription_service_medibridge.domain.enums.MedicineOrderStatus;
import com.medibridge.prescription_service_medibridge.domain.enums.PrescriptionStatus;
import com.medibridge.prescription_service_medibridge.event.KafkaProducer;
import com.medibridge.prescription_service_medibridge.event.dto.OrderCreatedEvent;
import com.medibridge.prescription_service_medibridge.event.dto.OrderStatusUpdatedEvent;
import com.medibridge.prescription_service_medibridge.repository.MedicineOrderRepository;
import com.medibridge.prescription_service_medibridge.repository.PrescriptionRepository;
import com.medibridge.prescription_service_medibridge.util.SecurityUtils;
import com.medibridge.prescription_service_medibridge.web.dto.OrderRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicineOrderService {

    private final MedicineOrderRepository orderRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final KafkaProducer kafkaProducer;

    @Transactional
    public MedicineOrder createOrder(OrderRequest request) {
        String patientId = SecurityUtils.getCurrentUserId();

        // 1. Validate Prescription
        Prescription prescription = prescriptionRepository.findById(UUID.fromString(request.getPrescriptionId()))
                .orElseThrow(() -> new EntityNotFoundException("Prescription not found"));

        if (!prescription.getPatientId().equals(patientId)) {
            throw new SecurityException("Cannot create order for another patient's prescription");
        }

        if (prescription.getStatus() != PrescriptionStatus.ISSUED) {
            throw new IllegalStateException("Prescription must be ISSUED to create an order");
        }

        // 2. Check strict 1-to-1 rule (simplified)
        // Production: Could allow re-orders if refill logic exists
        if (orderRepository.existsByPrescriptionId(prescription.getId())) {
            throw new IllegalStateException("Order already exists for this prescription");
        }

        // 3. Create Order
        MedicineOrder order = new MedicineOrder();
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setPrescriptionId(prescription.getId());
        order.setPatientId(patientId);
        order.setStatus(MedicineOrderStatus.CREATED);
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setContactPhone(request.getContactPhone());
        order.setPaymentStatus("PENDING");

        // 4. Copy Items
        prescription.getMedications().forEach(med -> {
            MedicineOrderItem item = new MedicineOrderItem();
            item.setMedicineName(med.getMedicineName());
            item.setMedicineCode(med.getMedicineCode());
            item.setQuantity(med.getQuantity());
            // item.setUnitPrice(...) - Requires catalog pricing service
            order.addItem(item);
        });

        // 5. Save
        MedicineOrder saved = orderRepository.save(order);

        // 6. Emit Event
        emitOrderCreatedEvent(saved);

        return saved;
    }

    @Transactional(readOnly = true)
    public Page<MedicineOrder> getMyOrders(String patientId, Pageable pageable) {
        return orderRepository.findByPatientId(patientId, pageable);
    }

    @Transactional
    public void updateStatus(String orderId, MedicineOrderStatus newStatus) {
        MedicineOrder order = orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        MedicineOrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);

        orderRepository.save(order);

        emitStatusUpdateEvent(order, oldStatus);
    }

    private void emitOrderCreatedEvent(MedicineOrder order) {
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(order.getId().toString())
                .orderNumber(order.getOrderNumber())
                .prescriptionId(order.getPrescriptionId().toString())
                .patientId(order.getPatientId())
                .totalAmount(order.getTotal())
                .itemsSummary(
                        order.getItems().stream().map(MedicineOrderItem::getMedicineName).collect(Collectors.toList()))
                .build();

        kafkaProducer.publish("medicine-order-created", event);
    }

    private void emitStatusUpdateEvent(MedicineOrder order, MedicineOrderStatus oldStatus) {
        OrderStatusUpdatedEvent event = OrderStatusUpdatedEvent.builder()
                .orderId(order.getId().toString())
                .patientId(order.getPatientId())
                .oldStatus(oldStatus)
                .newStatus(order.getStatus())
                .build();

        kafkaProducer.publish("medicine-order-status-updated", event);
    }
}
