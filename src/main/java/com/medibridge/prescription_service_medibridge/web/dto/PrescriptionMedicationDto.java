package com.medibridge.prescription_service_medibridge.web.dto;

import com.medibridge.prescription_service_medibridge.domain.enums.MedicineRoute;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrescriptionMedicationDto {
    private String id;

    @NotBlank(message = "Medicine name is required")
    private String medicineName;

    private String medicineCode;

    @NotBlank(message = "Dosage is required")
    private String dosage;

    @NotNull(message = "Route is required")
    private MedicineRoute route;

    @NotBlank(message = "Frequency is required")
    private String frequency;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer durationDays;

    @NotNull(message = "Quantity is required")
    @Min(value = 1)
    private Integer quantity;

    private String instructions;

    private boolean substitutionAllowed;
}
