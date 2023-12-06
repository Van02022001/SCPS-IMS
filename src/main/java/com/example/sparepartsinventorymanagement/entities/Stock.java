package com.example.sparepartsinventorymanagement.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "stock")
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;


    @Column(name = "opening_stock_quantity")
    private int openingStockQuantity;

    @Column(name = "opening_stock_value")
    private double openingStockValue;

    @Column(name = "closing_stock_quantity")
    private int closingStockQuantity;

    @Column(name = "closing_stock_value")
    private double closingStockValue;


    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

}
