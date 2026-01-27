package com.medibridge.prescription_service_medibridge.event.dto;

import com.medibridge.prescription_service_medibridge.domain.enums.MedicineOrderStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderStatusUpdatedEvent {
    private String orderId;
    private String patientId;
    private MedicineOrderStatus oldStatus;
    private MedicineOrderStatus newStatus;
}
