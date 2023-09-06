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
@Table(name = "item", indexes = {
        @Index(name = "idx_item_product", columnList = "productId"),
        @Index(name = "idx_item_brand", columnList = "brandId"),
        @Index(name = "idx_item_user", columnList = "supplierId"),
        @Index(name = "idx_item_order", columnList = "orderId")
})
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "brandId", nullable = false)
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "supplierId", nullable = false)
    private User supplier;

    @ManyToOne
    @JoinColumn(name = "orderId", nullable = false)
    private Order order;

    @Column(name = "sku", length = 100, nullable = false)
    private String sku;

    @Column(name = "mrp", nullable = false)
    private float mrp;

    @Column(name = "discount", nullable = false)
    private float discount;

    @Column(name = "price", nullable = false)
    private float price;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "sold", nullable = false)
    private int sold;

    @Column(name = "available", nullable = false)
    private int available;

    @Column(name = "defective", nullable = false)
    private int defective;

    @Column(name = "createdBy", nullable = false)
    private Long createdBy;

    @Column(name = "updatedBy")
    private Long updatedBy;

    @Column(name = "createdAt", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updatedAt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    // Getters and setters (omitted for brevity)
}