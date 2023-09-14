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
@Table(name = "order_item", indexes = {
        @Index(name = "idx_order_item_product", columnList = "product_id"),
        @Index(name = "idx_order_item_item", columnList = "item_id"),
        @Index(name = "idx_order_item_order", columnList = "order_id")
})
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "sku", length = 100, nullable = false)
    private String sku;

    @Column(name = "price", nullable = false)
    private float price;

    @Column(name = "discount", nullable = false)
    private float discount;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Getters and setters (omitted for brevity)
}