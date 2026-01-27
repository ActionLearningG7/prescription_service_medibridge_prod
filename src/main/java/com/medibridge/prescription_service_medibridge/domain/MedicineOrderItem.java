package com.medibridge.prescription_service_medibridge.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "medicine_order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private MedicineOrder order;

    // Snapshot of medicine details at time of order
    @Column(nullable = false)
    private String medicineName;

    private String medicineCode;

    @Column(nullable = false)
    private Integer quantity;

    // Future extension: Price calculation
    private BigDecimal unitPrice;
}
