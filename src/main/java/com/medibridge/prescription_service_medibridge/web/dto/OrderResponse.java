package com.medibridge.prescription_service_medibridge.web.dto;

import com.medibridge.prescription_service_medibridge.domain.enums.MedicineOrderStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private String id;
    private String orderNumber;
    private String prescriptionId;
    private String patientId;
    private MedicineOrderStatus status;
    private String deliveryAddress;
    private String contactPhone;
    private String paymentStatus;
    private BigDecimal subtotal;
    private BigDecimal deliveryFee;
    private BigDecimal total;
    private List<OrderItemDto> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
