package com.medibridge.prescription_service_medibridge.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumerService {

    @KafkaListener(topics = "doctor-suspended", groupId = "${spring.application.name}-group")
    public void handleDoctorSuspended(String event) {
        // TODO: Implement logic to prevent new prescriptions from this doctor
        // e.g., Update a local cache or 'DoctorStatus' table
        log.info("Received DoctorSuspended event: {}", event);
    }

    @KafkaListener(topics = "appointment-completed", groupId = "${spring.application.name}-group")
    public void handleAppointmentCompleted(String event) {
        // TODO: Logic to potentially notify doctor to create prescription if missing
        log.info("Received AppointmentCompleted event: {}", event);
    }
}
