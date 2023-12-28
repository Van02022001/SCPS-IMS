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


    @Column(name = "inbound_quantity", nullable = false)
    private int inboundQuantity; // Số lượng nhập

    @Column(name = "inbound_value", nullable = false)
    private double inboundValue; // Giá trị nhập

    @Column(name = "outbound_quantity")
    private int outboundQuantity; // Số lượng xuất

    @Column(name = "average_unit_value")
    private int averageUnitValue;

    @Column(name = "outbound_value")
    private double outboundValue; // Giá trị xuất

    @Column(name = "total_value")
    private double totalValue; // Giá trị xuất


    @Column(name = "total_quantity")
    private int totalQuantity;

    @Column(name = "available")
    private int available;

    @Column(name = "defective")
    private int defective;

    @Column(name = "discrepancy_quantity")
    private int discrepancyQuantity; // Sự khác biệt về số lượng

    @Column(name = "discrepancy_value")
    private double discrepancyValue;  // Sự khác biệt về giá trị



    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InventoryDiscrepancyLogs> discrepancyLogs;



}
