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
    private String itemCode;
    private String itemName;

    private int inboundQuantity; // Số lượng nhập


    private double inboundValue; // Giá trị nhập

    private int outboundQuantity; // Số lượng xuất


    private double outboundValue; // Giá trị xuất


    private double totalValue; // Giá trị xuất
    private int lost;
    private int defective;
    private int available;
    private int totalQuantity;
    private int averageUnitValue;






}
