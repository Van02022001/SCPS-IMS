package com.example.sparepartsinventorymanagement.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="customer_request_recipt_detail")
public class CustomerRequestReceiptDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="customer_request_detail_id")
    private Long id;

    @Column(name = "price")
    private double price;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "unit_name")
    private String unitName;

    @Column(name = "total_price")
    private double totalPrice;

    @ManyToOne
    @JoinColumn(name = "customer_request_id", nullable = false)
    private CustomerRequestReceipt customerRequestReceipt;


    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item items;

}
