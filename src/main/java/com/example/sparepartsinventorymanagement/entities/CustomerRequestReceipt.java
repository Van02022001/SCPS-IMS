package com.example.sparepartsinventorymanagement.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="customer_request_recipt")
public class CustomerRequestReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="customer_request_id")
    private Long id;


    @Column(name = "note")
    private String note;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdRequestBy;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedRequestBy;

    @ManyToOne
    @JoinColumn(name = "completed_by")
    private User completedRequestBy;


    @OneToMany(mappedBy = "customerRequestReceipt", cascade =CascadeType.ALL, orphanRemoval = true)
    private List<CustomerRequestReceiptDetail> customerRequestReceiptDetailList;


    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CustomerRequestReceiptStatus status;

}
