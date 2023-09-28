package com.example.sparepartsinventorymanagement.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="inventory_id")
    private Long id;

    @Column(name = "period", nullable = false)
    private String period; // Kỳ

    @Column(name = "opening_stock_quantity", nullable = false)
    private int openingStockQuantity; // Số lượng tồn đầu kỳ

    @Column(name = "opening_stock_value", nullable = false)
    private double openingStockValue; // Giá trị tồn đầu kỳ

    @Column(name = "closing_stock_quantity", nullable = false)
    private int closingStockQuantity; // Số lượng tồn cuối kỳ

    @Column(name = "closing_stock_value", nullable = false)
    private double closingStockValue; // Giá trị tồn cuối kỳ

    @Column(name = "inbound_quantity", nullable = false)
    private int inboundQuantity; // Số lượng nhập

    @Column(name = "inbound_value", nullable = false)
    private double inboundValue; // Giá trị nhập

    @Column(name = "outbound_quantity", nullable = false)
    private int outboundQuantity; // Số lượng xuất

    @Column(name = "outbound_value", nullable = false)
    private double outboundValue; // Giá trị xuất

    @Column(name = "total_value", nullable = false)
    private double totalValue; // Giá trị xuất


    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item; // Mối quan hệ với bảng Item




}
