package com.medibridge.prescription_service_medibridge.web.mapper;

import com.medibridge.prescription_service_medibridge.domain.MedicineOrder;
import com.medibridge.prescription_service_medibridge.domain.MedicineOrderItem;
import com.medibridge.prescription_service_medibridge.web.dto.OrderItemDto;
import com.medibridge.prescription_service_medibridge.web.dto.OrderResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse toResponse(MedicineOrder entity);

    OrderItemDto toItemDto(MedicineOrderItem entity);
}
