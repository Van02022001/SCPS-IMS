package com.example.sparepartsinventorymanagement.entities;

import com.example.sparepartsinventorymanagement.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="item_id")
    private Long id;

    @Column(name="code")
    private String code;

    @Column(name = "cost_price", nullable = false)
    private double costPrice;

    @Column(name = "sale_price", nullable = false)
    private double salePrice;


    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "sold", nullable = false)
    private int sold;

    @Column(name = "available", nullable = false)
    private int available;

    @Column(name = "defective", nullable = false)
    private int defective;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "updated_by_id", nullable = false)
    private User updatedBy;

    @Column(name="status", nullable = false)
    private ItemStatus status;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATETIME_FORMAT)
    private Date createdAt;

    @Column(name = "updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATETIME_FORMAT)
    private Date updatedAt;


    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @OneToMany(mappedBy = "item")
    private List<Inventory> inventoryList;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "origin_id", nullable = false)
    private Origin origin;

    @OneToOne(mappedBy = "item")
    private Location location;

    // Getters and setters (omitted for brevity)
}