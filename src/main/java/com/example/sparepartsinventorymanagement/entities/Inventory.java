package com.example.sparepartsinventorymanagement.entities;

import com.example.sparepartsinventorymanagement.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="inventory_id")
    private Long id;


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


    @Column(name = "discrepancy_quantity")
    private int discrepancyQuantity; // Sự khác biệt về số lượng

    @Column(name = "discrepancy_value")
    private double discrepancyValue;  // Sự khác biệt về giá trị

    @Column(name = "notes")
    private String notes;

    @ManyToMany
    @JoinTable(
            name = "inventory_item",
            joinColumns = @JoinColumn(name = "inventory_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items;

    @ManyToOne
    @JoinColumn(name = "period_id", nullable = false)
    private Period period; // Mối quan hệ mới với bảng Period

}
