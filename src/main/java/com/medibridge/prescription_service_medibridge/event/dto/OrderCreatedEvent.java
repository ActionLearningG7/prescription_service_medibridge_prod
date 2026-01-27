package com.medibridge.prescription_service_medibridge.event.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class OrderCreatedEvent {
    private String orderId;
    private String orderNumber;
    private String prescriptionId;
    private String patientId;
    private BigDecimal totalAmount;
    private List<String> itemsSummary;
}
