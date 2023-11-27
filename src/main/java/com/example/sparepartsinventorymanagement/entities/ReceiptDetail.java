package com.example.sparepartsinventorymanagement.entities;

import com.example.sparepartsinventorymanagement.audit.Auditable;
import com.example.sparepartsinventorymanagement.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@Table(name = "receipt_details")
public class ReceiptDetail  extends Auditable<User> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "quantity", nullable = false)
    private int quantity;


    @Column(name = "unit_name")
    private String unitName;

    @Column(name = "total_price")
    private double totalPrice;


    @Column(name = "total_quantity")
    private int totalQuantity;


    @Column(name = "description", columnDefinition = "TEXT")
    private String description;


    @ManyToOne
    @JoinColumn(name = "purchase_price_id")
    private PurchasePrice purchasePrice;

    @ManyToOne
    @JoinColumn(name = "pricing_id")
    private Pricing salePricice;
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "receipt_id", nullable = false)
    private Receipt receipt;

    @OneToMany(mappedBy = "receiptDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InventoryDiscrepancyLog> discrepancyLogs;

    public ReceiptDetail() {
        // Constructor mặc định cần thiết cho JPA
    }
}