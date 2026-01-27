package com.medibridge.prescription_service_medibridge.web.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemDto {
    private String id;
    private String medicineName;
    private String medicineCode;
    private Integer quantity;
    private BigDecimal unitPrice;
}
