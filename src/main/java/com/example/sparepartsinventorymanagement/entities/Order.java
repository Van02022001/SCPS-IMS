package com.example.sparepartsinventorymanagement.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "orders", indexes = {
        @Index(name = "idx_order_user", columnList = "user_id")
})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "type", nullable = false)
    private int type;

    @Column(name = "status", nullable = false)
    private int status;

    @Column(name = "sub_total", nullable = false)
    private float subTotal;

    @Column(name = "item_discount", nullable = false)
    private float itemDiscount;

    @Column(name = "tax", nullable = false)
    private float tax;


    @Column(name = "total", nullable = false)
    private float total;

    @Column(name = "promo", length = 50)
    private String promo;

    @Column(name = "discount", nullable = false)
    private float discount;

    @Column(name = "grand_total", nullable = false)
    private float grandTotal;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;
    // Getters and setters (omitted for brevity)
}