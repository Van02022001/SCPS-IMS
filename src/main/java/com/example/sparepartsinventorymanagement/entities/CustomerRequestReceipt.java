package com.example.sparepartsinventorymanagement.entities;

import com.example.sparepartsinventorymanagement.audit.Auditable;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@Entity
@SuperBuilder
@AllArgsConstructor
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")

@Table(name="customer_request_recipt")
public class CustomerRequestReceipt extends Auditable<User> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="customer_request_id")
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "note")
    private String note;


    @Column(name = "total_quantity")
    private int totalQuantity;

    @Column(name = "total_price")
    private double totalPrice;

    @OneToMany(mappedBy = "customerRequestReceipt", cascade =CascadeType.ALL, orphanRemoval = true)
    private List<CustomerRequestReceiptDetail> customerRequestReceiptDetailList;


    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CustomerRequestReceiptStatus status;


    @OneToMany(mappedBy = "customerRequestReceipt", fetch = FetchType.EAGER)
    private Set<Receipt> receipts;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;


    public CustomerRequestReceipt() {
        // Constructor mặc định cần thiết cho JPA
    }

}
