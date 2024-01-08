package com.example.sparepartsinventorymanagement.entities;

import com.example.sparepartsinventorymanagement.audit.Auditable;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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


//    @Column(name = "total_quantity")
//    private int totalQuantity;


    @Column(name = "description", columnDefinition = "TEXT")
    private String description;


    @Column(name = "unit_price")
    private double unitPrice;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "receipt_id", nullable = false)
    private Receipt receipt;

    @OneToMany(mappedBy = "receiptDetail", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ReceiptDiscrepancyLog> discrepancyLogs;
    @OneToMany(mappedBy = "receiptDetail", cascade = CascadeType.ALL)
    private List<ItemMovement> itemMovements;
    public ReceiptDetail() {
        // Constructor mặc định cần thiết cho JPA
    }
}