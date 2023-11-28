package com.example.sparepartsinventorymanagement.dto.response;

import com.example.sparepartsinventorymanagement.entities.Item;
import com.example.sparepartsinventorymanagement.entities.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InventoryDTO {

    private Long itemId;
    private String itemName;


    private int openingStockQuantity; // Số lượng tồn đầu kỳ

    private double openingStockValue; // Giá trị tồn đầu kỳ


    private int closingStockQuantity; // Số lượng tồn cuối kỳ

    private double closingStockValue; // Giá trị tồn cuối kỳ


    private int inboundQuantity; // Số lượng nhập


    private double inboundValue; // Giá trị nhập

    private int outboundQuantity; // Số lượng xuất


    private double outboundValue; // Giá trị xuất


    private double totalValue; // Giá trị xuất






}
