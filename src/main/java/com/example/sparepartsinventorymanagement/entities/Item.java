package com.example.sparepartsinventorymanagement.entities;

import com.example.sparepartsinventorymanagement.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Set;

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


    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "sold", nullable = false)
    private int sold;

    @Column(name = "available", nullable = false)
    private int available;

    @Column(name = "defective", nullable = false)
    private int defective;

    @Column(name = "min_stock_level", nullable = false)
    private int minStockLevel;

    @Column(name = "max_stock_level", nullable = false)
    private int maxStockLevel;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "updated_by_id")
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
    private SubCategory subCategory;

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

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemMovement> movements;

    @OneToMany(mappedBy = "items", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerRequestReceiptDetail> customerRequestReceiptDetailList;


    @OneToMany(mappedBy = "item")
    private Set<Pricing> pricings;

    @OneToMany(mappedBy = "item")
    private Set<PurchasePrice> purchasePrices;


    // Getters and setters (omitted for brevity)
}