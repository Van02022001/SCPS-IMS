package com.example.sparepartsinventorymanagement.entities;

import com.example.sparepartsinventorymanagement.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "items", indexes = {
        @Index(name = "idx_item_product", columnList = "product_id"),
        @Index(name = "idx_item_brand", columnList = "brand_id"),


})
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="item_id")
    private Long id;



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



    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

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
    @JoinColumn(name="warehouse_id", nullable = false)
    private Warehouse warehouse;


    @ManyToMany(mappedBy = "items")
    private List<Supplier> suppliers;




    // Getters and setters (omitted for brevity)
}