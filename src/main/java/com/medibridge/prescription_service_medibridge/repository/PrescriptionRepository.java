package com.medibridge.prescription_service_medibridge.repository;

import com.medibridge.prescription_service_medibridge.domain.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

    Optional<Prescription> findByPrescriptionNumber(String prescriptionNumber);

    Page<Prescription> findByPatientId(String patientId, Pageable pageable);

    Page<Prescription> findByDoctorId(String doctorId, Pageable pageable);

    Page<Prescription> findByDoctorIdAndStatus(String doctorId,
            com.medibridge.prescription_service_medibridge.domain.enums.PrescriptionStatus status, Pageable pageable);

    boolean existsByAppointmentId(String appointmentId);
}
