package com.example.sparepartsinventorymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InventoryItemSummaryDTO {
    private Long itemId;

    private int openingStockQuantitySum;
    private double openingStockValueSum;
    private int closingStockQuantitySum;
    private double closingStockValueSum;
    private int inboundQuantitySum;
    private double inboundValueSum;
    private int outboundQuantitySum;
    private double outboundValueSum;
    private double totalValueSum;
    private int discrepancyQuantitySum;
    private double discrepancyValueSum;
}
