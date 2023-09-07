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
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    private int type;

    @Column(name = "status", nullable = false)
    private int status;

    @Column(name = "subTotal", nullable = false)
    private float subTotal;

    @Column(name = "itemDiscount", nullable = false)
    private float itemDiscount;

    @Column(name = "tax", nullable = false)
    private float tax;

    @Column(name = "shipping", nullable = false)
    private float shipping;

    @Column(name = "total", nullable = false)
    private float total;

    @Column(name = "promo", length = 50)
    private String promo;

    @Column(name = "discount", nullable = false)
    private float discount;

    @Column(name = "grandTotal", nullable = false)
    private float grandTotal;

    @Column(name = "createdAt", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updatedAt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    // Getters and setters (omitted for brevity)
}