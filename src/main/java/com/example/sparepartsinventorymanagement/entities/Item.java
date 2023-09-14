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
        @Index(name = "idx_item_product", columnList = "product_id"),
        @Index(name = "idx_item_brand", columnList = "brand_id"),

        @Index(name = "idx_item_order", columnList = "order_id")
})
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="item_id")
    private Long id;

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

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;


    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;


    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;


    @ManyToOne
    @JoinColumn(name="warehouse_id", nullable = false)
    private Warehouse warehouse;


    // Getters and setters (omitted for brevity)
}