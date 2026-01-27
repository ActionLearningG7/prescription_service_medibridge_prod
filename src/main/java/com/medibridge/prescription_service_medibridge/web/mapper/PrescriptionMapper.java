package com.medibridge.prescription_service_medibridge.web.mapper;

import com.medibridge.prescription_service_medibridge.domain.Prescription;
import com.medibridge.prescription_service_medibridge.domain.PrescriptionMedication;
import com.medibridge.prescription_service_medibridge.web.dto.PrescriptionMedicationDto;
import com.medibridge.prescription_service_medibridge.web.dto.PrescriptionRequest;
import com.medibridge.prescription_service_medibridge.web.dto.PrescriptionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PrescriptionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "prescriptionNumber", ignore = true)
    @Mapping(target = "doctorId", ignore = true) // Set manually from security context
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "issuedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "medications", ignore = true) // Handled separately
    Prescription toEntity(PrescriptionRequest request);

    PrescriptionResponse toResponse(Prescription entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "prescription", ignore = true)
    PrescriptionMedication toMedicationEntity(PrescriptionMedicationDto dto);

    PrescriptionMedicationDto toMedicationDto(PrescriptionMedication entity);
}
