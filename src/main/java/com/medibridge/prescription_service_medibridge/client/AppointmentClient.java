package com.medibridge.prescription_service_medibridge.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "appointment-service")
public interface AppointmentClient {

    @GetMapping("/api/v1/appointments/{id}/exists")
    boolean verifyAppointmentExists(@PathVariable("id") String id);

    // In a real scenario, we'd fetch the appointment DTO to verify doctorId matches
    @GetMapping("/api/v1/appointments/{id}/verify-doctor/{doctorId}")
    boolean verifyAppointmentDoctor(@PathVariable("id") String id, @PathVariable("doctorId") String doctorId);
}
