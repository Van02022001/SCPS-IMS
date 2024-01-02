package com.example.sparepartsinventorymanagement.dto.response;

import com.example.sparepartsinventorymanagement.entities.ReceiptDiscrepancyLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiptDetailResponse {

    private Long id;
    private String itemName;
    private int quantity;
    private String unitName;
    private double price;
    private double totalPrice;
    private int discrepancyQuantity;
    private List<ReceiptDiscrepancyLog> discrepancyLogs;
}
