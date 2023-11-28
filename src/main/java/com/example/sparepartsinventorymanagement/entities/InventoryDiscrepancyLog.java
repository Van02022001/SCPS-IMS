package com.example.sparepartsinventorymanagement.entities;

import com.example.sparepartsinventorymanagement.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "inventory_discrepancy_log")
public class InventoryDiscrepancyLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "receipt_detail_id", nullable = false)
    private ReceiptDetail receiptDetail;

    @Column(name = "required_quantity")
    private int requiredQuantity;

    @Column(name = "actual_quantity")
    private int actualQuantity;

    @Column(name = "discrepancy_quantity")
    private int discrepancyQuantity;

    @Column(name = "discrepancy_value")
    private double discrepancyValue;

    @Column(name = "log_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATETIME_FORMAT)
    private Date logTime;


}