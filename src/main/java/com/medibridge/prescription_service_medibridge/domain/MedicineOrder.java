package com.medibridge.prescription_service_medibridge.domain;

import com.medibridge.prescription_service_medibridge.domain.enums.MedicineOrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "medicine_orders", indexes = {
        @Index(name = "idx_order_number", columnList = "orderNumber", unique = true),
        @Index(name = "idx_patient_id_order", columnList = "patientId"),
        @Index(name = "idx_prescription_id_order", columnList = "prescriptionId"), // To prevent duplicates
        @Index(name = "idx_status_order", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class MedicineOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String orderNumber;

    @Column(nullable = false)
    private UUID prescriptionId;

    @Column(nullable = false, length = 36)
    private String patientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicineOrderStatus status;

    @Column(columnDefinition = "TEXT")
    private String deliveryAddress;

    private String contactPhone;

    // Payment Info
    private String paymentStatus; // PENDING, PAID, FAILED (Simplification)
    private String paymentReference;

    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MedicineOrderItem> items = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    // Helper methods
    public void addItem(MedicineOrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}
