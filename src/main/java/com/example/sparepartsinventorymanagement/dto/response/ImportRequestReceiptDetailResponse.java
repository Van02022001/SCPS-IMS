package com.example.sparepartsinventorymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImportRequestReceiptDetailResponse {
    private Long id;
    private InfoItemDTO item;
    private int quantity;
    private String unitName;
    private double price;
    private double totalPrice;
    private int discrepancyQuantity;
    private List<ReceiptDiscrepancyLogResponse> discrepancyLogs;

}
