package com.medibridge.prescription_service_medibridge.repository;

import com.medibridge.prescription_service_medibridge.domain.MedicineOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MedicineOrderRepository extends JpaRepository<MedicineOrder, UUID> {

    Optional<MedicineOrder> findByOrderNumber(String orderNumber);

    Page<MedicineOrder> findByPatientId(String patientId, Pageable pageable);

    boolean existsByPrescriptionId(UUID prescriptionId);
}
