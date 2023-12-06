package com.example.sparepartsinventorymanagement.entities;

import com.example.sparepartsinventorymanagement.audit.Auditable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@AllArgsConstructor
@Getter
@Setter
@Table(name="customer_request_recipt_detail")
public class CustomerRequestReceiptDetail extends Auditable<User> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="customer_request_detail_id")
    private Long id;



    @Column(name = "quantity")
    private int quantity;

    @Column(name = "unit_name")
    private String unitName;


    @ManyToOne
    @JoinColumn(name = "customer_request_id", nullable = false)
    private CustomerRequestReceipt customerRequestReceipt;


    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item items;

    public CustomerRequestReceiptDetail() {
        // Constructor mặc định cần thiết cho JPA
    }
}
