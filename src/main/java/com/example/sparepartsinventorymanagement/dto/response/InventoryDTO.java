package com.example.sparepartsinventorymanagement.dto.response;

import com.example.sparepartsinventorymanagement.entities.Item;
import com.example.sparepartsinventorymanagement.entities.Period;
import com.example.sparepartsinventorymanagement.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InventoryDTO {

    private Long id;



    private int openingStockQuantity; // Số lượng tồn đầu kỳ

    private double openingStockValue; // Giá trị tồn đầu kỳ


    private int closingStockQuantity; // Số lượng tồn cuối kỳ

    private double closingStockValue; // Giá trị tồn cuối kỳ


    private int inboundQuantity; // Số lượng nhập


    private double inboundValue; // Giá trị nhập

    private int outboundQuantity; // Số lượng xuất


    private double outboundValue; // Giá trị xuất


    private double totalValue; // Giá trị xuất



    private int discrepancyQuantity; // Sự khác biệt về số lượng

    private double discrepancyValue;  // Sự khác biệt về giá trị


    private String notes;

    private Item item;

    private Period period;
}
