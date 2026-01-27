package com.medibridge.prescription_service_medibridge.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderRequest {

    @NotBlank(message = "Prescription ID is required")
    private String prescriptionId;

    @NotBlank(message = "Delivery Address is required")
    private String deliveryAddress;

    @NotBlank(message = "Contact Phone is required")
    private String contactPhone;
}
